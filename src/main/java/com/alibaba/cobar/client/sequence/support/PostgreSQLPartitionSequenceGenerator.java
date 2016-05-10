package com.alibaba.cobar.client.sequence.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.cobar.client.exception.ShardingException;
import com.alibaba.cobar.client.sequence.SequenceGenerator;

public class PostgreSQLPartitionSequenceGenerator implements SequenceGenerator,InitializingBean{
	
	private Log logger = LogFactory.getLog(PostgreSQLPartitionSequenceGenerator.class);

	private ConcurrentHashMap<String, Future<AtomicLong>> sequenceRepository = new ConcurrentHashMap<>();

	private volatile long boundaryMaxValue = 0;

	private long incrStep = 1000;

	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setIncrStep(long incrStep) {
		this.incrStep = incrStep;
	}
	
	@Override
	public long nextval(String name) {
		Future<AtomicLong> future = sequenceRepository.get(name);
		AtomicLong nextval = null;
		try {
			if (future == null) {
				FutureTask<AtomicLong> newFuture = new FutureTask<>(new DBSequenceBuilderTask(name));
				if (sequenceRepository.putIfAbsent(name, newFuture) == null) {
					newFuture.run();
				}
				if(null== (future = sequenceRepository.get(name))){
					return nextval(name);
				}
			}
			nextval = future.get();

		} catch (InterruptedException | ExecutionException e) {
			sequenceRepository.remove(name);
			throw new ShardingException(e);
		}

		long val = nextval.incrementAndGet();
		if(val<= boundaryMaxValue){
			if(val == boundaryMaxValue){
				sequenceRepository.remove(name);
			}
			return val;
		}
		for(;;){
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {}
			if(logger.isDebugEnabled()){
				logger.debug("sequence ["+name+"] outboundary, val="+val+",boundaryMaxValue="+boundaryMaxValue);
			}
			Future<AtomicLong> future2= sequenceRepository.get(name);
			if(future2!= future){
				return nextval(name);
			}
		}
	}

	private class DBSequenceBuilderTask implements Callable<AtomicLong> {

		private String name;

		DBSequenceBuilderTask(String name) {
			this.name = name;
		}

		@Override
		public AtomicLong call() throws Exception {
			int retry= 3;
			for (;;) {
				//the column name must be primary key;
				String sql = "select t.value from sequence_table t where t.name=? for update";
				Connection conn = PostgreSQLPartitionSequenceGenerator.this.dataSource.getConnection();
				conn.setAutoCommit(false);
				PreparedStatement ups = null;
				ResultSet rs = null;
				try(PreparedStatement ps = conn.prepareStatement(sql);){
					ps.setString(1, name);
					rs = ps.executeQuery();
					if (rs.next()) {
						long nextval = rs.getLong("value");
						sql = "update sequence_table set value=?, modify_time=? where name=? and value= ?";
						ups = conn.prepareStatement(sql);
						ups.setLong(1, nextval+ incrStep);
						ups.setLong(2, System.currentTimeMillis());
						ups.setString(3, name);
						ups.setLong(4, nextval);
						int result = ups.executeUpdate();
						conn.commit();
						if (result == 1) {
							if(logger.isInfoEnabled()){
								logger.info("update sequence name="+name+",value="+(nextval+ incrStep));
							}
							boundaryMaxValue= incrStep+ nextval;
							return new AtomicLong(nextval);
						}
						
					} else {
						sql = "insert into sequence_table(name,value,modify_time) values(?,?,?)";
						ups = conn.prepareStatement(sql);
						ups.setString(1, name);
						ups.setLong(2, incrStep);
						ups.setLong(3, System.currentTimeMillis());
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
					if (conn != null) {
						conn.setAutoCommit(true);
						try {
							conn.close();
						} catch (SQLException e) {}
					}
					if (ups != null) {
						try {
							ups.close();
						} catch (SQLException e) {}
					}
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {}
					}
					if((retry--)==0){
						throw new Exception("cannot build sequence for name: "+name);
					}
				}
			}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String sql= "select count(1) from pg_tables where tablename='sequence_table'";
		try(Connection conn= dataSource.getConnection();
				Statement st=conn.createStatement();
				ResultSet rs= st.executeQuery(sql);
				){
			if(rs.next() && 0== rs.getInt(1)){
				sql= "CREATE TABLE sequence_table(" 
						+ "name varchar(64) NOT NULL,"
						+ "value numeric NOT NULL,"
						+ "modify_time numeric NOT NULL,"
						+ "PRIMARY KEY(name)"
						+")";
				boolean result= st.execute(sql);
				logger.info("create sequence_table result: "+result);
			}
		}catch(Exception e){
			logger.error(e);
		}
	}

}
