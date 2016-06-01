package io.pddl.router.table;

import java.util.Collection;

public interface GlobalTableRepository {

	boolean isGlobalTableEmpty();
		
	Collection<String> getDBPartitionNames(String tableName);
	
	Collection<String> getGobalTableNames();
}
