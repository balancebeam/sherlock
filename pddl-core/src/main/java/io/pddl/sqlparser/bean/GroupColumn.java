package io.pddl.sqlparser.bean;

public class GroupColumn implements IndexColumn{
	
	private String columnName;
	
	private int columnIndex;
	
	public GroupColumn(String columnName,int columnIndex){
		this.columnName= columnName;
		this.columnIndex= columnIndex;
	}
	
	public String getColumnName(){
		return columnName;
	}
	
	public int getColumnIndex(){
		return columnIndex;
	}
	
	@Override
	public String toString(){
		return "{columnName="+columnName+",columnIndex="+columnIndex+"}";
	}
}
