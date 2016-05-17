package com.alibaba.cobar.client.datasources;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;


public class CobarDataSourceProxy extends LazyConnectionDataSourceProxy implements DataSource,IPartitionDataSourceContext{
	
	private int weight;
	
	private PartitionDataSource partitionDataSource;
	
	public CobarDataSourceProxy(DataSource delegate){
		super(delegate);
	}
	
	public int getWeight(){
		return weight;
	}
	
	void setWeight(int weight){
		this.weight= weight;
	}
	
	@Override
	public PartitionDataSource getPartitionDataSource() {
		return partitionDataSource;
	}
	
	void setPartitionDataSource(PartitionDataSource partitionDataSource){
		this.partitionDataSource= partitionDataSource;
	}
	
	@Override
	public Connection getConnection() throws SQLException{
		return super.getConnection();
	}

}
