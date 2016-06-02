package io.pddl.testcase.entity;

public class ItemCondition {

	private long userId;

	private long[] itemIds;
	
	private long orderId;
	
	public void setItemIds(long[] itemIds){
		this.itemIds= itemIds;
	}
	
	public long[] getItemIds(){
		return itemIds;
	}
	
	public void setUserId(long userId){
		this.userId= userId;
	}
	
	public long getUserId(){
		return userId;
	}
	
	public void setOrderId(long orderId){
		this.orderId= orderId;
	}
	
	public long getOrderId(){
		return orderId;
	}
	
}
