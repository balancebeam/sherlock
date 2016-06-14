package io.pddl.datasource.support.strategy;

import javax.sql.DataSource;

import io.pddl.datasource.PartitionDataSource;

public class PollingReadStrategyWithWriteSupport extends PollingReadStrategySupport{
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource pds) {
		return getDataSourceByPolling(pds,1);
	}
	
	@Override
	public String getStrategyName(){
		return "polling-w";
	}

}
