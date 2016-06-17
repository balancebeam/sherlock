package io.pddl.sequence.support;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

public abstract class AbstractDBPartitionSequenceGenerator extends AbstractPartitionSequenceGenerator{
	
	protected DataSource dataSource;
	
	protected String sequenceTable= "sequence_table";
	
	protected String sequenceColumn= "name";
	
	protected String maxValueColumn= "value";
	
	protected String modifyTimeColumn= "modify_time";
	
	protected String nodeNameColumn= "node_name";
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

//	public void setSequenceTableName(String sequenceTableName){
//		this.sequenceTableName= sequenceTableName;
//	}
//	
//	public void setSequenceColumnName(String sequenceColumnName){
//		this.sequenceColumnName= sequenceColumnName;
//	}
//	
//	public void setMaxValueColumnName(String maxValueColumnName){
//		this.maxValueColumnName= maxValueColumnName;
//	}
	
	@Override
	public Callable<AtomicLong> takeNewBatchSequence(String name){
		return new DBSequenceBuilderTask(name);
	}

	private class DBSequenceBuilderTask implements Callable<AtomicLong> {

		private String name;

		private int retry= 5;
		
		DBSequenceBuilderTask(String name) {
			this.name = name;
		}

		@Override
		public AtomicLong call() throws Exception {
			String ip = InetAddress.getLocalHost().getHostAddress();
			for (;;) {
				//the column name must be primary key;
				String sql = "select t."+maxValueColumn+" from "+sequenceTable+" t where t."+sequenceColumn+"=? for update";
				Connection conn = dataSource.getConnection();
				conn.setAutoCommit(false);
				PreparedStatement ps= null,ups = null;
				ResultSet rs = null;
				try{
					ps = conn.prepareStatement(sql);
					ps.setString(1, name);
					rs = ps.executeQuery();
					if (rs.next()) {
						long oldMaxValue = rs.getLong(maxValueColumn),
							newMaxValue= oldMaxValue+ incrStep;
						
						sql = "update "+sequenceTable+" set "+maxValueColumn+"=?, "+modifyTimeColumn+"=?, "+nodeNameColumn+
								"=? where "+sequenceColumn+"=? and "+maxValueColumn+"= ?";
						ups = conn.prepareStatement(sql);
						ups.setLong(1, newMaxValue);
						ups.setDate(2, new Date(System.currentTimeMillis()));
						ups.setString(3, ip);
						ups.setString(4, name);
						ups.setLong(5, oldMaxValue);
						int result = ups.executeUpdate();
						conn.commit();
						if (result == 1) {
							if(logger.isInfoEnabled()){
								logger.info("update sequence name="+name+",value="+(newMaxValue));
							}
							boundaryMaxValue= newMaxValue;
							return new AtomicLong(oldMaxValue);
						}
						
					} else {
						sql = "insert into "+sequenceTable+"("+sequenceColumn+","+maxValueColumn+","+modifyTimeColumn+","+nodeNameColumn+") values(?,?,?,?)";
						ups = conn.prepareStatement(sql);
						ups.setString(1, name);
						ups.setLong(2, incrStep);
						//ups.setLong(3, System.currentTimeMillis());
						ups.setDate(3, new Date(System.currentTimeMillis()));
						ups.setString(4, ip);
						int result = ups.executeUpdate();
						conn.commit();
						if (result == 1) {
							if(logger.isInfoEnabled()){
								logger.info("new sequence name="+name+",value="+incrStep);
							}
							boundaryMaxValue= incrStep;
							return new AtomicLong(0);
						}
					}
				}
				catch(SQLException e){
					conn.rollback();
					logger.error(e.getMessage(),e);
				}
				finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(ps);
					DbUtils.closeQuietly(ups);
					if (conn != null) {
						conn.setAutoCommit(true);
						DbUtils.closeQuietly(conn);
					}
					if((retry--)==0){
						throw new Exception("Cannot generate sequence for name: "+name);
					}
				}
			}
		}
	}
}
