package com.alibaba.cobar.client.wrapper;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

import com.alibaba.cobar.client.datasources.CobarDataSourceDescriptor;
import com.alibaba.cobar.client.datasources.IDataSourceDescriptorContext;


public class CobarDataSourceWrapper extends DelegatingDataSource implements DataSource,IDataSourceDescriptorContext{
	
	private int weight= 100;
	
	private CobarDataSourceDescriptor dataSourceDescriptor;
	
	public CobarDataSourceWrapper(CobarDataSourceDescriptor dataSourceDescriptor,DataSource delegate){
		super(delegate);
		this.dataSourceDescriptor= dataSourceDescriptor;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public void setWeight(int weight){
		this.weight= weight;
	}
	
	@Override
	public CobarDataSourceDescriptor getDataSourceDescriptor() {
		return dataSourceDescriptor;
	}

}
