package io.anyway.sherlock.router.table.config;

import java.util.List;

import io.anyway.sherlock.router.strategy.config.ShardingStrategyConfig;
import io.anyway.sherlock.router.table.LogicTable;

public abstract class AbstractLogicTableConfig implements LogicTable {

	private String name;

	private String primaryKey;
	
	private List<? extends LogicTable> children;
	
	private LogicTable parent;
	
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
	public ShardingStrategyConfig getDataSourceStrategyConfig(){
		return parent.getDataSourceStrategyConfig();
	}
	
	@Override
	public boolean isChildTable(){
		return this instanceof LogicChildTableConfig;
	}
	
	@Override
	public List<String> getPartitionDataSourceNames(){
		return parent.getPartitionDataSourceNames();
	}

	@Override
	public String toString(){
		StringBuilder builder= new StringBuilder();
		builder.append("{ ")
		.append("tableName=")
		.append(getName())
		.append(", ")
		.append("tablePostfixes=")
		.append(getTablePostfixes())
		.append(", ")
		.append("dataSourceStrategy=")
		.append(getDataSourceStrategyConfig())
		.append(", ")
		.append("tableStrategy=")
		.append(getTableStrategyConfig())
		.append(" }");
		return builder.toString();
	}
}
