package io.pddl.executor;

import com.alibaba.cobar.client.sqlparser.SQLParsedResult;

import io.pddl.datasource.PartitionDataSourceContext;

public interface ExecutorContext extends PartitionDataSourceContext,Cloneable{

	final public static int OP_SELECT = 1 << 0;
	final public static int OP_PERSISTENCE = 1 << 1;
	final public static int OP_TRANSACTION = 1 << 2;
	
	int getOperationType();
	
	boolean isSelectable();
	
	boolean isPersistent();
	
	boolean isTransactional();
	
	String getStatementName();
	
	Object getParameterObject();
	
	Object getAttribute(String key);
	
	SQLParsedResult getSQLParsedResult();
	
}
