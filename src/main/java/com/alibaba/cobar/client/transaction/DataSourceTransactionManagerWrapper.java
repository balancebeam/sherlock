package com.alibaba.cobar.client.transaction;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class DataSourceTransactionManagerWrapper extends DataSourceTransactionManager{

	public DataSourceTransactionManagerWrapper(DataSource dataSource){
		super(dataSource);
	}
	
	@Override
	protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
		//do nothing
	}
	
	private void cleanupAfterCompletion(DefaultTransactionStatus status) {
		
	}
}
