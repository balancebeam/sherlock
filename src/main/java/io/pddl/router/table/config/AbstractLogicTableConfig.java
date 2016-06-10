package io.pddl.router.table.config;

import java.util.List;

import io.pddl.router.strategy.config.ShardingStrategyConfig;
import io.pddl.router.table.LogicTable;

public abstract class AbstractLogicTableConfig implements LogicTable{

	private String name;
	
	private String primaryKey;
	
	private List<? extends LogicTable> children;
	
	private LogicTable parent;
	
	private String hierarchical;
	
	public void setName(String name){
		this.name= name;
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	public void setPrimaryKey(String primaryKey){
		this.primaryKey= primaryKey;
	}
	
	@Override
	public String getPrimaryKey(){
		return primaryKey;
	}
	
	public void setChildren(List<? extends LogicTable> children){
		this.children= children;
	}
	
	@Override
	public List<? extends LogicTable> getChildren(){
		return children;
	}
	
	public void setParent(LogicTable parent){
		this.parent= parent;
	}
	
	@Override
	public LogicTable getParent(){
		return parent;
	}
	
	@Override
	public List<String> getTablePostfixes(){
		return parent.getTablePostfixes();
	}
	
	@Override
	public ShardingStrategyConfig getTableStrategyConfig(){
		return parent.getTableStrategyConfig();
	}
	
	@Override
	public ShardingStrategyConfig getDatabaseStrategyConfig(){
		return parent.getDatabaseStrategyConfig();
	}
	
	@Override
	public boolean isChildTable(){
		return this instanceof LogicChildTableConfig;
	}
	
	public void setHierarchical(String hierarchical){
		this.hierarchical= hierarchical;
	}
	
	@Override
	public String getHierarchical(){
		return hierarchical;
	}
	
	@Override
	public String toString(){
		StringBuilder builder= new StringBuilder();
		builder.append("[ ")
		.append("tableName=")
		.append(getName())
		.append(", ")
		.append("tablePostfixes=")
		.append(getTablePostfixes())
		.append(", ")
		.append("databaseStrategy=")
		.append(getDatabaseStrategyConfig())
		.append(", ")
		.append("tableStrategy=")
		.append(getTableStrategyConfig())
		.append(" ]");
		return builder.toString();
	}
}
