package io.pddl.cache;

public interface ShardingCache {
	
	String getLogicTablePostfix(String tableName,String primaryKey,Object value);
	
	void putLocalTablePostfix(String tableName,String primaryKey,Object value,String postfix);
	
	
}
