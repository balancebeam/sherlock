package io.pddl.datasource;

import javax.sql.DataSource;

public interface DatabaseReadStrategy {
	
	String getStrategyName();
	
	DataSource getReadDataSource(PartitionDataSource ds);
	
}
