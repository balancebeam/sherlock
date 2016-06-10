package io.pddl.datasource.support.strategy;

import javax.sql.DataSource;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DatabaseReadStrategy;

public class OnlyWriteReadStrategySupport implements DatabaseReadStrategy{
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource ds) {
		return ds.getMasterDataSource();
	}
	
	@Override
	public String getStrategyName(){
		return "only-write";
	}
}
