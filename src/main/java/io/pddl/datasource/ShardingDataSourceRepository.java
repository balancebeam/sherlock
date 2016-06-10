package io.pddl.datasource;

import java.util.Set;

public interface ShardingDataSourceRepository {
	
	Set<String> getPartitionDataSourceNames();
	
	PartitionDataSource getPartitionDataSource(String name);
	
	PartitionDataSource getDefaultPartitionDataSource();
}
