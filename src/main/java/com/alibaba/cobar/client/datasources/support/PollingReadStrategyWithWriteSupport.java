package com.alibaba.cobar.client.datasources.support;

import javax.sql.DataSource;

import com.alibaba.cobar.client.datasources.PartitionDataSource;

public class PollingReadStrategyWithWriteSupport extends PollingReadStrategySupport{
	
	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return getDataSourceByPolling(ds,1);
	}

}
