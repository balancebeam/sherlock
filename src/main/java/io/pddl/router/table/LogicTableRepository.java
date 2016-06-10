package io.pddl.router.table;

import java.util.Collection;

public interface LogicTableRepository {

	boolean isLogicChildTable(String tableName);
	
	LogicTable getLogicTable(String tableName);
	
	Collection<String> getLogicTableNames();
}
