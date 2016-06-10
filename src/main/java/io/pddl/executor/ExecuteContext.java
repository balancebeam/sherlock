package io.pddl.executor;

import java.util.Collection;

import io.pddl.jdbc.ShardingConnection;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.bean.SQLStatementType;

public interface ExecuteContext{

	ShardingConnection getShardingConnection();
	
	boolean isDQLWithoutTransaction();
	
	boolean isDMLOperation();
	
	SQLStatementType getStatementType();
	
	SQLParsedResult getSQLParsedResult();
	
	String getLogicSql();
	
	Collection<String> getAvailableDatabaseNames();
}
