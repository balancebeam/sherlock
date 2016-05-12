package com.alibaba.cobar.client.sequence.support;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.cobar.client.exception.ShardingException;
import com.alibaba.cobar.client.sequence.SequenceGenerator;

public abstract class AbstractDBPartitionSequenceGenerator implements SequenceGenerator{
	
	protected Log logger = LogFactory.getLog(getClass());

	protected ConcurrentHashMap<String, Future<AtomicLong>> sequenceRepository = new ConcurrentHashMap<>();

	protected volatile long boundaryMaxValue = 0;

	protected long incrStep = 1000;

	protected DataSource dataSource;
	
	protected String sequenceTable= "sequence_table";
	
	protected String sequenceColumn= "name";
	
	protected String maxValueColumn= "value";
	
	protected String modifyTimeColumn= "modify_time";
	
	protected String nodeNameColumn= "node_name";
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setIncrStep(long incrStep) {
		this.incrStep = incrStep;
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
	public long nextval(final String name) {
		Future<AtomicLong> future = sequenceRepository.get(name);
		AtomicLong nextval = null;
		if (future == null) {
			FutureTask<AtomicLong> newFuture = new FutureTask<>(new DBSequenceBuilderTask(name));
			if (sequenceRepository.putIfAbsent(name, newFuture) == null) {
				newFuture.run();
			}
			if((future= sequenceRepository.get(name))== null){
				return nextval(name);
			}
		}
		try {
			nextval = future.get();
		} catch (Exception e) {
			sequenceRepository.remove(name);
			throw new ShardingException(e);
		}

		long val = nextval.incrementAndGet();
		if(val< boundaryMaxValue){
			return val;
		}
		else if(val== boundaryMaxValue){
			sequenceRepository.remove(name);
			return val;
		}
		
		//out boundary
		for(;;){
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {}
			if(logger.isDebugEnabled()){
				logger.debug("sequence ["+name+"] outboundary, val="+val+",boundaryMaxValue="+boundaryMaxValue);
			}
			Future<AtomicLong> nFuture= sequenceRepository.get(name);
			if(nFuture!= future){
				return nextval(name);
			}
		}
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
						ups.setLong(2, System.currentTimeMillis());
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
						ups.setLong(3, System.currentTimeMillis());
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
					logger.error(e);
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
