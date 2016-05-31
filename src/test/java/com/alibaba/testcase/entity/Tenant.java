package com.alibaba.testcase.entity;

import java.sql.Date;

public class Tenant {

	private long id;
	
	private String name;
	
	private String telephone;
	
	private Date updTime;
	
	public void setId(long id){
		this.id= id;
	}
	
	public long getId(){
		return id;
	}
	
	public void setName(String name){
		this.name= name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setTelephone(String telephone){
		this.telephone= telephone;
	}
	
	public String getTelephone(){
		return telephone;
	}
	
	public void setUpdTime(Date updTime){
		this.updTime= updTime;
	}
	
	public Date getUpdTime(){
		return updTime;
	}
}
