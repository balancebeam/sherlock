package io.pddl.testcase.entity;

public class ItemExt {
	
	private long extId;

	private long itemId;
	
	private String status;
	
	public ItemExt(){}
	
	public ItemExt(long extId,long itemId,String status){
		this.extId= extId;
		this.itemId= itemId;
		this.status= status;
	}
	
	public void setItemId(long itemId){
		this.itemId= itemId;
	}
	
	public long getItem(){
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
