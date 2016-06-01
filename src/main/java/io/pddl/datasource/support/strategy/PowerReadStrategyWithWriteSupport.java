package io.pddl.datasource.support.strategy;

import javax.sql.DataSource;

import io.pddl.datasource.PartitionDataSource;

public class PowerReadStrategyWithWriteSupport extends PowerReadStrategySupport{

	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return getDataSourceByPower(ds,1);
	}
	
	@Override
	public String getStrategyName(){
		return "power-w";
	}

}
