package io.pddl.testcase.entity;

public class ItemExt {
	
	private long extId;

	private long itemId;
	
	private String status;
	
	private long userId;
	
	public ItemExt(){}
	
	public void setUserId(long userId){
		this.userId= userId;
	}
	
	public long getUserId(){
		return userId;
	}
	
	public void setItemId(long itemId){
		this.itemId= itemId;
	}
	
	public long getItemId(){
		return itemId;
	}
	
	public void setExtId(long extId){
		this.extId= extId;
	}
	
	public long getExtId(){
		return extId;
	}
	
	public void setStatus(String status){
		this.status= status;
	}
	
	public String getStatus(){
		return status;
	}
}
