package io.anyway.sherlock.datasource.support.strategy;

import javax.sql.DataSource;

import io.anyway.sherlock.datasource.PartitionDataSource;

public class RoundRobinStrategyWithMasterSupport extends RoundRobinStrategySupport {
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource pds) {
		return getDataSourceByCycle(pds,1);
	}
	
	@Override
	public String getStrategyName(){
		return "roundRobin-m";
	}

}
