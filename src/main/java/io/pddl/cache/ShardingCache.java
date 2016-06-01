package io.pddl.cache;

import io.pddl.router.database.support.IBatisRoutingFact;
import io.pddl.router.database.support.RoutingResult;

public interface ShardingCache {
	
	String getLogicTablePostfix(String tableName,String primaryKey,Object value);
	
	void putLocalTablePostfix(String tableName,String primaryKey,Object value,String postfix);
	
	RoutingResult getDatabaseRoutingResult(IBatisRoutingFact routingFact);
	
	void putDatabaseRoutingResult(IBatisRoutingFact routingFact,RoutingResult result);
	
	RoutingResult getDatabaseRoutingResultByGlobalTable(IBatisRoutingFact routingFact);
	
	void putDatabaseRoutingResultByGlobalTable(IBatisRoutingFact routingFact,RoutingResult result);
}
