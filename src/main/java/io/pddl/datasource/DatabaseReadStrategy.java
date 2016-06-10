package io.pddl.datasource;

import javax.sql.DataSource;

public interface DatabaseReadStrategy {
	
	String getStrategyName();
	
	DataSource getSlaveDataSource(PartitionDataSource ds);
	
}
