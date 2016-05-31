package io.pddl.testcase.entity;

public class Order {

	private long userId;
	
	private long orderId;
	
	private String status;
	
	public Order(){}
	
	public Order(long userId,long orderId,String status){
		this.userId= userId;
		this.orderId= orderId;
		this.status= status;
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
