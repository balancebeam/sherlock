package io.pddl.datasource.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

public class DefaultDataSourceProxy extends LazyConnectionDataSourceProxy implements DataSource{
	
	private int weight;
	
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
	public Connection getConnection() throws SQLException{
		return super.getConnection();
	}

}
