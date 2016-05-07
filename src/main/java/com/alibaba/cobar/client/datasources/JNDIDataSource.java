package com.alibaba.cobar.client.datasources;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class JNDIDataSource implements DataSource{
	
	private DataSource delegate;
	
	public void setName(String name){
		try {
			Context context= new InitialContext();
			delegate= (DataSource)context.lookup(name);
		} catch (NamingException e) {
			throw new IllegalArgumentException("Error jndi name: "+name);
		}
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
		return delegate.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return delegate.getConnection(username, password);
	}

}
