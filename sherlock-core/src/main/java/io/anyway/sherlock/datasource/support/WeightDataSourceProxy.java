package io.anyway.sherlock.datasource.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

/**
 * 带权重的数据源
 * @author yangzz
 *
 */
public class WeightDataSourceProxy extends LazyConnectionDataSourceProxy implements DataSource{
	
	private int weight= 100;
	
	public WeightDataSourceProxy(DataSource delegate){
		super(delegate);
	}
	
	public int getWeight(){
		return weight;
	}
	
	public void setWeight(int weight){
		if(weight<1){
			weight= 100;
		}
		this.weight= weight;
	}
	
	@Override
	public Connection getConnection() throws SQLException{
		return super.getConnection();
	}

}
