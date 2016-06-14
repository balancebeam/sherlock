package io.pddl.router.datasource.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.exception.ShardingDataSourceException;
import io.pddl.executor.ExecuteContext;
import io.pddl.router.datasource.DataSourceRouter;
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
public class DataSourceRouterSupport extends AbstractRouterSupport implements DataSourceRouter{
	
	private ShardingDataSourceRepository shardingDataSourceRepository;
	
	public void setShardingDataSourceRepository(ShardingDataSourceRepository shardingDataSourceRepository){
		this.shardingDataSourceRepository= shardingDataSourceRepository;
	}
	
	@Override
	public Collection<String> doRoute(ExecuteContext ctx) {
		List<List<LogicTable>> logicTables= parseLogicTables(ctx);
		return doMultiDataSourceSharding(ctx,logicTables);
	}
	
	private Collection<String> doMultiDataSourceSharding(ExecuteContext ctx,List<List<LogicTable>> logicTables) {
		List<Collection<String>> dataSourceNames = new ArrayList<Collection<String>>();
		for (List<LogicTable> tables : logicTables) {
tables: 	for (int i = 0; i < tables.size(); i++) {
				LogicTable logicTable = tables.get(i);
				String layerIdx = logicTable.getLayerIdx();
				for (int j = 0; j < i; j++) {
					if (layerIdx.startsWith(tables.get(j).getLayerIdx())) {
						if(logger.isInfoEnabled()){
							logger.info("table ["+ logicTable.getName()+"] will use table ["+tables.get(j).getName() +"] sharding strategy");
						}
						continue tables;
					}
				}
				Collection<String> candidateNames= doSingleDataSourceSharding(ctx,logicTable);
				if(!CollectionUtils.isEmpty(candidateNames)){
					if(logger.isInfoEnabled()){
						logger.info("table ["+ logicTable.getName()+"] candidate dataSource names: "+candidateNames);
					}
					dataSourceNames.add(candidateNames);
				}
			}
		}
		if(logger.isInfoEnabled()){
			logger.info("found candidate dataSource names: "+dataSourceNames);
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
				throw new ShardingDataSourceException("can not shard dataSource for sql: " +ctx.getLogicSql());
			}
			if(logger.isInfoEnabled()){
				logger.info("no suitable dataSource，will use all available partition dataSource "+ctx.getAvailableDataSourceNames());
			}
			return ctx.getAvailableDataSourceNames();
		}
		return result;
	}
	
	private Collection<String> doSingleDataSourceSharding(ExecuteContext ctx,LogicTable logicTable) {
		ShardingStrategyConfig strategyConfig = logicTable.getDataSourceStrategyConfig();
		if(strategyConfig== null){
			return null;
		}
		List<ShardingValue<?>> shardingValues = getShardingValues(ctx,logicTable.getName(),strategyConfig.getColumns());
		if(CollectionUtils.isEmpty(shardingValues)){
			return null;
		}
		return strategyConfig.getStrategy().doSharding(ctx,shardingDataSourceRepository.getPartitionDataSourceNames(), shardingValues);
	}
}
