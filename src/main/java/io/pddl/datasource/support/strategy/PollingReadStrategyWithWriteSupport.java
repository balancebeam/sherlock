package io.pddl.datasource.support.strategy;

import javax.sql.DataSource;

import io.pddl.datasource.PartitionDataSource;

public class PollingReadStrategyWithWriteSupport extends PollingReadStrategySupport{
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource ds) {
		return getDataSourceByPolling(ds,1);
	}
	
	@Override
	public String getStrategyName(){
		return "polling-w";
	}

}
