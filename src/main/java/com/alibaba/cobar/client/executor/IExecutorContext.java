package com.alibaba.cobar.client.executor;

import com.alibaba.cobar.client.datasources.IDataSourceDescriptorContext;

public interface IExecutorContext extends IDataSourceDescriptorContext{

	final public static int OP_READ = 1 << 0;
	final public static int OP_WRITE = 1 << 1;
	final public static int OP_TRANSACTION = 1 << 2;
	
	int getOperationType();
	
	boolean isReadable();
	
	boolean isWritable();
	
	boolean isTransactional();
	
	Object getAttribute(String key);
	
	void setAttribute(String key,Object value);
}
