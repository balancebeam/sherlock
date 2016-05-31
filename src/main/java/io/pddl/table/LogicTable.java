package io.pddl.table;

import java.util.List;

import io.pddl.table.model.LogicTableStrategyConfig;

public interface LogicTable {

	String getName();
	
	String getPrimaryKey();
	
	boolean matchPartition(String partition);
	
	List<String> getPostfixes();
	
	LogicTable getParent();
	
	List<? extends LogicTable> getChildren();
	
	LogicTableStrategyConfig getStrategyConfig();
	
	boolean isPrimaryTable();
	
	String getHierarchical();
}
