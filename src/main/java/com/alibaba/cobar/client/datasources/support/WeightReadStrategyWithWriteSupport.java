package com.alibaba.cobar.client.datasources.support;

import javax.sql.DataSource;

import com.alibaba.cobar.client.datasources.CobarDataSourceProxy;
import com.alibaba.cobar.client.datasources.PartitionDataSource;

public class WeightReadStrategyWithWriteSupport extends WeightReadStrategySupport{
	
	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return getDataSourceByWeight(ds,((CobarDataSourceProxy)ds.getWriteDataSource()).getWeight());
	}
}
