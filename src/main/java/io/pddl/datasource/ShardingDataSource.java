package io.pddl.datasource;

import java.util.Set;

public interface ShardingDataSource {
	
	Set<String> getPartitionDataSourceNames();
	
	PartitionDataSource getPartitionDataSource(String name);
	
	PartitionDataSource getDefaultPartitionDataSource();
}
