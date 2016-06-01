package io.pddl.datasource;

import javax.sql.DataSource;

public interface PartitionDataSource{
   
	String getName();
	
	DataSource getWriteDataSource();
	
	DataSource getReadDataSource();
	
	int getPoolSize();
}
