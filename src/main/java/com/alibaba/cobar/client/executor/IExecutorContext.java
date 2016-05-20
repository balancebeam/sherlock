package com.alibaba.cobar.client.executor;

import com.alibaba.cobar.client.datasources.IPartitionDataSourceContext;

public interface IExecutorContext extends IPartitionDataSourceContext{

	final public static int OP_SELECT = 1 << 0;
	final public static int OP_PERSISTENCE = 1 << 1;
	final public static int OP_TRANSACTION = 1 << 2;
	
	int getOperationType();
	
	boolean isSelectable();
	
	boolean isPersistent();
	
	boolean isTransactional();
	
	Object getAttribute(String key);
	
	void setAttribute(String key,Object value);
}
