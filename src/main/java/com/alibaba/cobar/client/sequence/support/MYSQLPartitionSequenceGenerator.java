package com.alibaba.cobar.client.sequence.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.beans.factory.InitializingBean;

public class MYSQLPartitionSequenceGenerator extends AbstractDBPartitionSequenceGenerator implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		String sql = "select count(1) from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"+sequenceTable+"'";

		try (Connection conn = dataSource.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sql);) {
			if (rs.next() && 0 == rs.getInt(1)) {
				sql = "CREATE TABLE " + sequenceTable + "(" 
							+ sequenceColumn + " varchar(128) NOT NULL,"
							+ maxValueColumn + " decimal(10,0) DEFAULT NULL," 
							+ modifyTimeColumn + " datetime DEFAULT NULL,"
							+ nodeNameColumn + " varchar(128) DEFAULT NULL,"
							+ "PRIMARY KEY (name)" + ")";
				boolean result = st.execute(sql);
				logger.info("create " + sequenceTable + " result:" + result);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
