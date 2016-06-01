package io.pddl.datasource.support.strategy;

import javax.sql.DataSource;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DatabaseReadStrategy;

public class OnlyWriteReadStrategySupport implements DatabaseReadStrategy{
	
	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return ds.getWriteDataSource();
	}
	
	@Override
	public String getStrategyName(){
		return "only-write";
	}
}
