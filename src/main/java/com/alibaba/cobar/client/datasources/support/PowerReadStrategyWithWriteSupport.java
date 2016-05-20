package com.alibaba.cobar.client.datasources.support;

import javax.sql.DataSource;

import com.alibaba.cobar.client.datasources.PartitionDataSource;

public class PowerReadStrategyWithWriteSupport extends PowerReadStrategySupport{

	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return getDataSourceByPower(ds,1);
	}

}
