package io.pddl.sqlparser.bean;

public class OrderColumn {
	
	public enum OrderType{
		ASC,DESC;
	}
	
	private int columnIndex;
	
	private String columnName;
	
	private OrderType orderType;
	
	public OrderColumn(String columnName, OrderType orderType,int columnIndex){
		this.columnName= columnName;
		this.orderType= orderType;
		this.columnIndex= columnIndex;
	}
	
	public int getColumnIndex(){
		return columnIndex;
	}
	
	public String getColumnName(){
		return columnName;
	}
	
	public OrderType getOrderType(){
		return orderType;
	}
	
	@Override
	public String toString(){
		return "{name="+columnName+",columnIndex="+columnIndex+",orderType="+orderType+"}";
	}
}
