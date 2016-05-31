package io.pddl.table.model;

import java.util.ArrayList;
import java.util.List;

import io.pddl.table.LogicPrimaryTable;
import io.pddl.table.LogicTable;

public abstract class AbstractLogicTableConfig implements LogicTable{

	private String name;
	
	private String primaryKey;
	
	private List<? extends LogicTable> children;
	
	private LogicTable parent;
	
	private List<String> actualTableNames;
	
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
	public List<String> getPartitions(){
		return parent.getPartitions();
	}
	
	@Override
	public List<String> getPostfixes(){
		return parent.getPostfixes();
	}
	
	@Override
	public LogicTableStrategyConfig getStrategyConfig(){
		return parent.getStrategyConfig();
	}
	
	@Override
	public List<String> getActualTableNames(){
		if(actualTableNames== null){
			makeupActualTable();
		}
		return actualTableNames;
	}
	
	private synchronized void makeupActualTable(){
		if(actualTableNames== null){
			actualTableNames= new ArrayList<String>();
			for(String postfix: getPostfixes()){
				actualTableNames.add(name+postfix);
			}
		}
	}
	
	@Override
	public boolean isPrimaryTable(){
		return this instanceof LogicPrimaryTable;
	}
	
	public void setHierarchical(String hierarchical){
		this.hierarchical= hierarchical;
	}
	
	@Override
	public String getHierarchical(){
		return hierarchical;
	}
}
