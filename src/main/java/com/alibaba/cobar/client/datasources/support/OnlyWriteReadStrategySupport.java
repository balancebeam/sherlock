package com.alibaba.cobar.client.datasources.support;

import javax.sql.DataSource;

import com.alibaba.cobar.client.datasources.IPartitionReadStrategy;
import com.alibaba.cobar.client.datasources.PartitionDataSource;

public class OnlyWriteReadStrategySupport implements IPartitionReadStrategy{
	
	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return ds.getWriteDataSource();
	}
}
