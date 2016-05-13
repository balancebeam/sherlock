package com.alibaba.cobar.client.executor.support;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cobar.client.datasources.DataSourceDescriptor;
import com.alibaba.cobar.client.executor.IExecutorContext;

public class ExecutorContextSupporter implements IExecutorContext{

	private int operationType= 0;
	
	private DataSourceDescriptor dataSourceDescriptor;
	
	private Map<String,Object> attributes;
	
	@Override
	public DataSourceDescriptor getDataSourceDescriptor() {
		return dataSourceDescriptor;
	}

	@Override
	public int getOperationType() {
		return operationType;
	}
	
	public void setOperationType(int op){
		operationType= op;
	}
	
	public void setDataSourceDescriptor(DataSourceDescriptor dataSourceDescriptor){
		this.dataSourceDescriptor= dataSourceDescriptor;
	}

	@Override
	public boolean isReadable() {
		return operationType == OP_READ;
	}

	@Override
	public boolean isWritable() {
		return (operationType & OP_WRITE) != 0;
	}

	@Override
	public boolean isTransactional() {
		return (operationType & OP_TRANSACTION) != 0;
	}

	@Override
	public Object getAttribute(String key) {
		if(attributes== null){
			return null;
		}
		return attributes.get(key);
	}
	
	@Override
	public void setAttribute(String key,Object value){
		if(attributes== null){
			attributes= new HashMap<String,Object>();
		}
		attributes.put(key, value);
	}

}
