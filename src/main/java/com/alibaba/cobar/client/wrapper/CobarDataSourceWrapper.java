package com.alibaba.cobar.client.wrapper;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.alibaba.cobar.client.datasources.DataSourceDescriptor;
import com.alibaba.cobar.client.datasources.IDataSourceDescriptorContext;


public class CobarDataSourceWrapper extends LazyConnectionDataSourceProxy implements DataSource,IDataSourceDescriptorContext{
	
	private int weight= 100;
	
	private DataSourceDescriptor dataSourceDescriptor;
	
	public CobarDataSourceWrapper(DataSourceDescriptor dataSourceDescriptor,DataSource delegate){
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
	public DataSourceDescriptor getDataSourceDescriptor() {
		return dataSourceDescriptor;
	}

}
