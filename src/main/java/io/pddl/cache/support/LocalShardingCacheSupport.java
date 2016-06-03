package io.pddl.cache.support;

import io.pddl.cache.ShardingCache;
import io.pddl.router.database.support.IBatisRoutingFact;
import io.pddl.router.database.support.RoutingResult;

import java.util.HashMap;
import java.util.Map;

public class LocalShardingCacheSupport implements ShardingCache{

    private Map<String,Object> cache= new HashMap<String,Object>();

    @Override
    public String getLogicTablePostfix(String tableName, String primaryKey, Comparable<?> value) {
        String key= tableName+"#"+primaryKey+"#"+value;
        return (String) cache.get(key);
    }

    @Override
    public void putLocalTablePostfix(String tableName, String primaryKey, Comparable<?> value, String postfix) {
        String key= tableName+"#"+primaryKey+"#"+value;
        cache.put(key,postfix);
    }

    @Override
    public RoutingResult getDatabaseRoutingResult(IBatisRoutingFact routingFact) {
        return (RoutingResult)cache.get(routingFact.getAction());
    }

    @Override
    public void putDatabaseRoutingResult(IBatisRoutingFact routingFact, RoutingResult result) {
        cache.put(routingFact.getAction(),result);
    }

    @Override
    public RoutingResult getDatabaseRoutingResultByGlobalTable(IBatisRoutingFact routingFact) {
        return (RoutingResult)cache.get(routingFact.getAction()+"#globalTable");
    }

    @Override
    public void putDatabaseRoutingResultByGlobalTable(IBatisRoutingFact routingFact, RoutingResult result) {
        cache.put(routingFact.getAction()+"#globalTable",result);
    }
}
