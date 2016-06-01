package io.pddl.datasource.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.PartitionDataSourceContext;


public class DefaultDataSourceProxy extends LazyConnectionDataSourceProxy implements DataSource,PartitionDataSourceContext{
	
	private int weight;
	
	private PartitionDataSource partitionDataSource;
	
	public DefaultDataSourceProxy(DataSource delegate){
		super(delegate);
	}
	
	public int getWeight(){
		return weight;
	}
	
	public void setWeight(int weight){
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
