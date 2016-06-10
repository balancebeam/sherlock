package io.pddl.router.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import io.pddl.cache.ShardingCache;
import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteHolder;
import io.pddl.router.strategy.value.ShardingCollectionValue;
import io.pddl.router.strategy.value.ShardingRangeValue;
import io.pddl.router.strategy.value.ShardingSingleValue;
import io.pddl.router.strategy.value.ShardingValue;
import io.pddl.router.table.LogicTable;
import io.pddl.router.table.LogicTableRepository;
import io.pddl.sqlparser.bean.Condition;
import io.pddl.sqlparser.bean.Table;

public abstract class AbstractRouterSupport{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected LogicTableRepository logicTableRepository;
	
	protected ShardingCache shardingCache;
	
	public void setLogicTableRepository(LogicTableRepository logicTableRepository) {
		this.logicTableRepository = logicTableRepository;
	}
	
	public void setShardingCache(ShardingCache shardingCache){
		this.shardingCache= shardingCache;
	}

	protected List<List<LogicTable>> parseLogicTables(ExecuteContext ctx) {
		@SuppressWarnings("unchecked")
		List<List<LogicTable>> result= (List<List<LogicTable>>)ExecuteHolder.getAttribute("tableRouter.parseLogicTables");
		if(result!= null){
			return result;
		}
		Set<Table> tables= ctx.getSQLParsedResult().getTables();
		Map<String, List<LogicTable>> hash = new HashMap<String, List<LogicTable>>(tables.size());
		for (Table table: tables) {
			String tableName= table.getName();
			LogicTable logicTable = logicTableRepository.getLogicTable(tableName);
			//if not logic table
			if (logicTable == null) {
				continue;
			}
			String key = logicTable.getHierarchical().split(",")[0];
			List<LogicTable> list = hash.get(key);
			if (list == null) {
				hash.put(key, list= new ArrayList<LogicTable>());
			}
			list.add(logicTable);
		}
		result = new ArrayList<List<LogicTable>>(hash.size());
		for (List<LogicTable> list: hash.values()) {
			Collections.sort(list, new Comparator<LogicTable>() {
				@Override
				public int compare(LogicTable o1, LogicTable o2) {
					String[] h1 = o1.getHierarchical().split(",");
					String[] h2 = o2.getHierarchical().split(",");
					int result = h1.length - h2.length;
					if (result == 0) {
						for (int i = 0; i < h1.length; i++) {
							if (0 != (result = Integer.parseInt(h1[i]) - Integer.parseInt(h2[i]))) {
								return result;
							}
						}
					}
					return result;
				}
			});
			result.add(list);
		}
		ExecuteHolder.setAttribute("tableRouter.parseLogicTables", result);
		return result;
	}
	
	protected List<ShardingValue<?>> getShardingValues(ExecuteContext ctx,String logicTableName,List<String> columns){
		List<ShardingValue<?>> shardingValues = new ArrayList<ShardingValue<?>>(columns.size());
		for (String column : columns) {
			Condition condition = getCondition(ctx,logicTableName, column);
			if (condition != null) {
				//now only support =、in、between operator
				switch(condition.getOperator()){
					case EQUAL:
						shardingValues.add(new ShardingSingleValue<Comparable<?>>(column, condition.getValues()));
						break;
					case IN:
						shardingValues.add(new ShardingCollectionValue<Comparable<?>>(column, condition.getValues()));
						break;
					case BETWEEN:
						shardingValues.add(new ShardingRangeValue<Comparable<?>>(column, condition.getValues()));
						break;
					default:
						throw new UnsupportedOperationException(condition.getOperator().toString());
				}
			}
		}
		return shardingValues;
	}
	
	//find Condition by tablename and column
	protected Condition getCondition(ExecuteContext ctx,String tableName, String column) {
		Optional<Condition> option= ctx.getSQLParsedResult().getCondition().find(tableName, column);
		return option.isPresent()? option.get(): null;
	}
}
