package io.pddl.sqlparser.bean;

public class OrderColumn {
	
	public enum OrderType{
		ASC,DESC;
	}
	
	private int index;
	
	private String name;
	
	private OrderType orderType;
	
	public OrderColumn(int index,String name, OrderType orderType){
		this.index= index;
		this.name= name;
		this.orderType= orderType;
	}
	
	public int getIndex(){
		return index;
	}
	
	public String getName(){
		return name;
	}
	
	public OrderType getOrderType(){
		return orderType;
	}
	
	@Override
	public String toString(){
		return "{name="+name+",index="+index+",orderType="+orderType+"}";
	}
}
