package io.pddl.table;

import java.util.List;

import io.pddl.table.model.LogicTableStrategyConfig;

public interface LogicTable {

	String getName();
	
	String getPrimaryKey();
	
	List<String> getPartitions();
	
	List<String> getPostfixes();
	
	LogicTable getParent();
	
	List<? extends LogicTable> getChildren();
	
	LogicTableStrategyConfig getStrategyConfig();
	
	List<String> getActualTableNames();
	
	boolean isPrimaryTable();
	
	String getHierarchical();
}
