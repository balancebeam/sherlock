package io.pddl.router.table;

import java.util.List;

import io.pddl.router.strategy.config.ShardingStrategyConfig;

public interface LogicTable {

	String getName();
	
	String getPrimaryKey();
	
	List<String> getTablePostfixes();
	
	LogicTable getParent();
	
	List<? extends LogicTable> getChildren();
	
	ShardingStrategyConfig getTableStrategyConfig();
	
	ShardingStrategyConfig getDatabaseStrategyConfig();
	
	boolean isChildTable();
	
	String getHierarchical();
}
