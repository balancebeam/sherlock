package io.pddl.testcase.entity;

public class OrderCondition {

	private long userId;

	private long[] orderIds;
	
	public void setUserId(long userId){
		this.userId= userId;
	}
	
	public long getUserId(){
		return userId;
	}
	
	public void setOrderIds(long[] orderIds){
		this.orderIds= orderIds;
	}
	
	public long[] getOrderIds(){
		return orderIds;
	}
}
