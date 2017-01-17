package io.pddl.router.database.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import io.pddl.exception.ShardingDataSourceException;
import io.pddl.executor.ExecuteContext;
import io.pddl.router.database.DatabaseRouter;
import io.pddl.router.strategy.config.ShardingStrategyConfig;
import io.pddl.router.strategy.value.ShardingValue;
import io.pddl.router.support.AbstractRouterSupport;
import io.pddl.router.table.LogicTable;
import io.pddl.sqlparser.bean.SQLStatementType;

/**
 * 数据源路由实现
 * @author yangzz
 *
 */
public class DatabaseRouterSupport extends AbstractRouterSupport implements DatabaseRouter{
	
	@Override
	public Collection<String> doRoute(ExecuteContext ctx) {
		List<List<LogicTable>> logicTables= parseLogicTables(ctx);
		return doMultiDatabaseSharding(ctx,logicTables);
	}
	
	private Collection<String> doMultiDatabaseSharding(ExecuteContext ctx,List<List<LogicTable>> logicTables) {
		List<Collection<String>> databaseNames = new ArrayList<Collection<String>>();
		for (List<LogicTable> tables : logicTables) {
loop: 	for (int i = 0; i < tables.size(); i++) {
				LogicTable logicTable = tables.get(i);
				String layerIdx = logicTable.getLayerIdx();
				for (int j = 0; j < i; j++) {
					if (layerIdx.startsWith(tables.get(j).getLayerIdx())) {
						if(logger.isInfoEnabled()){
							logger.info("table ["+ logicTable.getName()+"] will use table ["+tables.get(j).getName() +"] sharding strategy");
						}
						continue loop;
					}
				}
				Collection<String> candidateNames= doDatabaseSharding(ctx,logicTable);
				if(!CollectionUtils.isEmpty(candidateNames)){
					if(logger.isInfoEnabled()){
						logger.info("table ["+ logicTable.getName()+"] candidate database names: "+candidateNames);
					}
					databaseNames.add(candidateNames);
				}
			}
		}
		if(logger.isInfoEnabled()){
			logger.info("found candidate database names: "+databaseNames);
		}
		List<String> result= null;
		if(!CollectionUtils.isEmpty(databaseNames)){
			result = new ArrayList<String>(ctx.getShardingDataSourceRepository().getPartitionDataSourceNames());
			for(Collection<String> each: databaseNames){
				//取交集
				result.retainAll(each);
			}
		}
		if(CollectionUtils.isEmpty(result)){
			if(ctx.getStatementType()== SQLStatementType.INSERT){
				throw new ShardingDataSourceException("can not shard database for sql: " +ctx.getLogicSql());
			}
			if(logger.isInfoEnabled()){
				logger.info("no suitable database, will use all available database: "+ctx.getShardingDataSourceRepository().getPartitionDataSourceNames());
			}
			return ctx.getShardingDataSourceRepository().getPartitionDataSourceNames();
		}
		return result;
	}
	
	private Collection<String> doDatabaseSharding(ExecuteContext ctx,LogicTable logicTable) {
		ShardingStrategyConfig strategyConfig = logicTable.getDataSourceStrategyConfig();
		if(strategyConfig== null){
			return logicTable.getPartitionDataSourceNames();
		}
		List<List<ShardingValue<?>>> values = getShardingValues(ctx,logicTable.getName(),strategyConfig.getColumns());
		//对于数据库应该是确定的
		if(CollectionUtils.isEmpty(values)){
			return logicTable.getPartitionDataSourceNames();
		}
		Collection<String> result= doSharding(strategyConfig.getStrategy(),ctx,ctx.getShardingDataSourceRepository().getPartitionDataSourceNames(), values);
		if(!CollectionUtils.isEmpty(logicTable.getPartitionDataSourceNames())){
			List<String> sameNames= new ArrayList<String>(logicTable.getPartitionDataSourceNames());
			sameNames.retainAll(result);
			return sameNames;
		}
		return result;
	}
}
