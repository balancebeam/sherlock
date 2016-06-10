package io.pddl.cache;

public interface ShardingCache {
	
	String getLogicTablePostfix(String tableName,String primaryKey,Comparable<?> value);
	
	void putLocalTablePostfix(String tableName,String primaryKey,Comparable<?> value,String postfix);
	
}
