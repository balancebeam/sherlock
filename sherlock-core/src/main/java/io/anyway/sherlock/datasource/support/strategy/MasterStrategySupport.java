package io.anyway.sherlock.datasource.support.strategy;

import javax.sql.DataSource;

import io.anyway.sherlock.datasource.DataSourceReadStrategy;
import io.anyway.sherlock.datasource.PartitionDataSource;

public class MasterStrategySupport implements DataSourceReadStrategy {
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource pds) {
		return pds.getMasterDataSource();
	}
	
	@Override
	public String getStrategyName(){
		return "master";
	}
}
