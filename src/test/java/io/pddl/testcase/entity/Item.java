package io.pddl.testcase.entity;

public class Item {

	private long userId;

	private long itemId;
	
	private long orderId;
	
	private String status;
	
	public void setItemId(long itemId){
		this.itemId= itemId;
	}
	
	public long getItemId(){
		return itemId;
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
	
	public void setStatus(String status){
		this.status= status;
	}
	
	public String getStatus(){
		return status;
	}
}
