package com.alibaba.cobar.client.wrapper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.alibaba.cobar.client.datasources.CobarDataSourceDescriptor;
import com.alibaba.cobar.client.datasources.IDataSourceDescriptorContext;


public class CobarDataSourceWrapper implements DataSource,IDataSourceDescriptorContext{
	
	private DataSource delegate;
	
	private int weight= 100;
	
	private CobarDataSourceDescriptor dataSourceDescriptor;
	
	public CobarDataSourceWrapper(CobarDataSourceDescriptor dataSourceDescriptor,DataSource delegate){
		this.dataSourceDescriptor= dataSourceDescriptor;
		this.delegate= delegate;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public void setWeight(int weight){
		this.weight= weight;
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return delegate.getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return delegate.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return delegate.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return delegate.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return delegate.getConnection(username, password);
	}

	@Override
	public CobarDataSourceDescriptor getDataSourceDescriptor() {
		return dataSourceDescriptor;
	}

}
