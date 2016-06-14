package io.pddl.datasource.support.strategy;

import javax.sql.DataSource;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.support.WeightDataSourceProxy;

public class WeightReadStrategyWithWriteSupport extends WeightReadStrategySupport{
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource ds) {
		return getDataSourceByWeight(ds,((WeightDataSourceProxy)ds.getMasterDataSource()).getWeight());
	}
	
	@Override
	public String getStrategyName(){
		return "weight-w";
	}
}
