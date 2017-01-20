package io.anyway.sherlock.datasource.support.strategy;

import javax.sql.DataSource;

import io.anyway.sherlock.datasource.PartitionDataSource;
import io.anyway.sherlock.datasource.support.WeightDataSourceProxy;

public class WeightStrategyWithMasterSupport extends WeightStrategySupport {
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource pds) {
		return getDataSourceByWeight(pds,((WeightDataSourceProxy)pds.getMasterDataSource()).getWeight());
	}
	
	@Override
	public String getStrategyName(){
		return "weight-m";
	}
}
