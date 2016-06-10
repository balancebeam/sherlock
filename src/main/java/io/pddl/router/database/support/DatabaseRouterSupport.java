package io.pddl.router.database.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.exception.ShardingDatabaseException;
import io.pddl.executor.ExecuteContext;
import io.pddl.router.database.DatabaseRouter;
import io.pddl.router.strategy.config.ShardingStrategyConfig;
import io.pddl.router.strategy.value.ShardingValue;
import io.pddl.router.support.AbstractRouterSupport;
import io.pddl.router.table.LogicTable;
import io.pddl.sqlparser.bean.SQLStatementType;

public class DatabaseRouterSupport extends AbstractRouterSupport implements DatabaseRouter{
	
	private ShardingDataSourceRepository shardingDataSourceRepository;
	
	public void setShardingDataSourceRepository(ShardingDataSourceRepository shardingDataSourceRepository){
		this.shardingDataSourceRepository= shardingDataSourceRepository;
	}
	
	@Override
	public Collection<String> doRoute(ExecuteContext ctx) {
		List<List<LogicTable>> logicTables= parseLogicTables(ctx);
		return doMultiDatabaseSharding(ctx,logicTables);
	}
	
	private Collection<String> doMultiDatabaseSharding(ExecuteContext ctx,List<List<LogicTable>> logicTables) {
		List<Collection<String>> dataSourceNames = new ArrayList<Collection<String>>();
		for (List<LogicTable> tables : logicTables) {
tables: 	for (int i = 0; i < tables.size(); i++) {
				LogicTable logicTable = tables.get(i);
				String hierarchical = logicTable.getHierarchical();
				for (int j = 0; j < i; j++) {
					if (hierarchical.startsWith(tables.get(j).getHierarchical())) {
						continue tables;
					}
				}
				Collection<String> names= doSingleLogicTableSharding(ctx,logicTable);
				if(names!= null){
					dataSourceNames.add(names);
				}
			}
		}
		List<String> result= null;
		if(!CollectionUtils.isEmpty(dataSourceNames)){
			result = new ArrayList<String>(shardingDataSourceRepository.getPartitionDataSourceNames());
			for(Collection<String> each: dataSourceNames){
				result.retainAll(each);
			}
		}
		if(CollectionUtils.isEmpty(result)){
			if(ctx.getStatementType()== SQLStatementType.INSERT){
				throw new ShardingDatabaseException("can not shard database for sql: " +ctx.getLogicSql());
			}
			return ctx.getAvailableDatabaseNames();
		}
		return result;
	}
	
	private Collection<String> doSingleLogicTableSharding(ExecuteContext ctx,LogicTable logicTable) {
		ShardingStrategyConfig strategyConfig = logicTable.getDatabaseStrategyConfig();
		if(strategyConfig== null){
			return null;
		}
		List<ShardingValue<?>> shardingValues = getShardingValues(ctx,logicTable.getName(),strategyConfig.getColumns());
		if(CollectionUtils.isEmpty(shardingValues)){
			return null;
		}
		return strategyConfig.getStrategy().doSharding(shardingDataSourceRepository.getPartitionDataSourceNames(), shardingValues);
	}
}
