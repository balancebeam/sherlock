package io.pddl.datasource.support.strategy;

import javax.sql.DataSource;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DataSourceReadStrategy;

public class MasterStrategySupport implements DataSourceReadStrategy{
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource pds) {
		return pds.getMasterDataSource();
	}
	
	@Override
	public String getStrategyName(){
		return "master";
	}
}
