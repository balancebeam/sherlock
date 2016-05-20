package com.alibaba.cobar.client.datasources;

import javax.sql.DataSource;

public interface IPartitionReadStrategy {
	
	DataSource getReadDataSource(PartitionDataSource ds);
}
