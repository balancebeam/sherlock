package io.pddl.router.table;

import java.util.Collection;

public interface LogicTableRepository {

	boolean isLogicTableEmpty();
	
	boolean isLogicPrimaryTable(String tableName);
	
	LogicTable getLogicTable(String tableName);
	
	Collection<String> getLogicTableNames();
}
