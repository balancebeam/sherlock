package io.pddl.sequence.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.InitializingBean;

public class PGPartitionSequenceGenerator extends AbstractDBPartitionSequenceGenerator implements InitializingBean{
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Connection conn= dataSource.getConnection();
		Statement st= null;
		ResultSet rs= null;
		
		try{
			String sql= "select count(1) from pg_tables where tablename='"+sequenceTable+"'";
			st=conn.createStatement();
			rs= st.executeQuery(sql);
			if(rs.next() && 0== rs.getInt(1)){
				sql= "CREATE TABLE "+sequenceTable+"(" 
						+ sequenceColumn+ " varchar(64) NOT NULL,"
						+ maxValueColumn+ " numeric NOT NULL,"
						+ modifyTimeColumn+ " numeric NOT NULL,"
						+ nodeNameColumn+ " varchar(64) NOT NULL,"
						+ "PRIMARY KEY("+sequenceColumn+")"
						+")";
				boolean result= st.execute(sql);
				logger.info("create sequence_table result: "+result);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		finally{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
			DbUtils.closeQuietly(conn);
		}
	}

}
