package io.pddl.datasource;

import javax.sql.DataSource;

public interface PartitionDataSource{
   
	String getName();
	
	DataSource getMasterDataSource();
	
	DataSource getSlaveDataSource();
	
	int getPoolSize();
	
	int getTimeout();
}
