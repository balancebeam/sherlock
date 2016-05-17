package com.alibaba.cobar.client.datasources;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

public class DataSourceDescriptor implements FactoryBean<DataSource>{
	
	private DataSource dataSource;
	
	private int weight= 100 ;
	
	public void setDataSource(DataSource dataSource){
		this.dataSource= dataSource;
	}

	public void setWeight(int weight){
		this.weight= weight;
	}
	
	@Override
	public DataSource getObject() throws Exception {
		Assert.notNull(dataSource);
		CobarDataSourceProxy dataSourceWrapper= new CobarDataSourceProxy(dataSource);
		dataSourceWrapper.setWeight(weight);
		return dataSourceWrapper;
	}

	@Override
	public Class<?> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
