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

public class MYSQLPartitionSequenceGenerator implements SequenceGenerator,InitializingBean{

	private Log logger = LogFactory.getLog(MYSQLPartitionSequenceGenerator.class);
	
	private ConcurrentHashMap<String, Future<AtomicLong>> sequenceRepository = new ConcurrentHashMap<>();

	private volatile long boundaryMaxValue = 0;

	private long incrStep = 1000;

	private DataSource dataSource;
	
	private int retry = 3;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setIncrStep(long incrStep) {
		this.incrStep = incrStep;
	}
	
	public void setRetry(int retry){
		this.retry = retry;
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
			if(logger.isDebugEnabled()){
				logger.debug("sequence ["+name+"] outboundary, val="+val+",boundaryMaxValue="+boundaryMaxValue);
			}
			Future<AtomicLong> future2= sequenceRepository.get(name);
			if(future2!= future){
				return nextval(name);
			}
		}		
		
	}
	
	class DBSequenceBuilderTask implements Callable<AtomicLong> {

		private String name;

		DBSequenceBuilderTask(String name) {
			this.name = name;
		}

		@Override
		public AtomicLong call() throws Exception {

			while(true){
				String sql = "select t.value from sequence_table t where t.name=? for update";
				Connection conn = MYSQLPartitionSequenceGenerator.this.dataSource.getConnection();
				conn.setAutoCommit(false);
				PreparedStatement ups = conn.prepareStatement(sql);
				ResultSet rs = null;
				try{
					ups.setString(1, name);
					rs = ups.executeQuery();
					if(rs.next()){
						long nextval = rs.getLong("value");
						sql = "update sequence_table set value=?, modify_time=? where name=? and value= ?";
						if(ups != null){
							ups.close();
						}
						ups = conn.prepareStatement(sql);
						ups.setLong(1, nextval+ incrStep);
						ups.setLong(2, System.currentTimeMillis());
						ups.setString(3, name);
						ups.setLong(4, nextval);
						int result = ups.executeUpdate();
						conn.commit();
						if(result == 1){
							if(logger.isInfoEnabled()){
								logger.info("update sequence name="+name+",value="+(nextval+ incrStep));
							}
							boundaryMaxValue= incrStep+ nextval;
							return new AtomicLong(nextval);
						}
						
					}else{
						sql = "insert into sequence_table(name,value,modify_time) values(?,?,?)";
						if(ups != null){
							ups.close();
						}
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
				
				}catch(SQLException e){
					conn.rollback();
					logger.error(e);
				}finally{
					if(conn != null){
						conn.close();
					}
					if(ups != null){
						ups.close();
					}
					if(rs != null){
						rs.close();
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
		String sql= "select count(1) from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='sequence_table'";
		
		try(Connection conn= dataSource.getConnection();
				Statement st=conn.createStatement();
				ResultSet rs= st.executeQuery(sql);
				){
			if(rs.next() && 0== rs.getInt(1)){
				sql= "CREATE TABLE sequence_table ("
						+"name varchar(128) NOT NULL,"
						+"value decimal(10,0) DEFAULT NULL,"
						+"modify_time datetime DEFAULT NULL,"
						+"PRIMARY KEY (name)"
						+")";
				boolean result= st.execute(sql);
				logger.info("create sequence_table result: "+result);
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
}
