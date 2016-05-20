package com.alibaba.cobar.client.executor.support;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cobar.client.datasources.PartitionDataSource;
import com.alibaba.cobar.client.executor.IExecutorContext;

public class ExecutorContextSupporter implements IExecutorContext{

	private int operationType= 0;
	
	private PartitionDataSource partitionDataSource;
	
	private Map<String,Object> attributes;
	
	@Override
	public PartitionDataSource getPartitionDataSource() {
		return partitionDataSource;
	}

	@Override
	public int getOperationType() {
		return operationType;
	}
	
	public void setOperationType(int op){
		operationType= op;
	}
	
	public void setPartitioinDataSource(PartitionDataSource partitionDataSource){
		this.partitionDataSource= partitionDataSource;
	}

	@Override
	public boolean isSelectable() {
		return operationType == OP_SELECT;
	}

	@Override
	public boolean isPersistent() {
		return (operationType & OP_PERSISTENCE) != 0;
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
