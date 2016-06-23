package io.pddl.router.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Optional;

import io.pddl.cache.ShardingCache;
import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteHolder;
import io.pddl.router.strategy.value.ShardingCollectionValue;
import io.pddl.router.strategy.value.ShardingRangeValue;
import io.pddl.router.strategy.value.ShardingSingleValue;
import io.pddl.router.strategy.value.ShardingValue;
import io.pddl.router.table.LogicTable;
import io.pddl.sqlparser.bean.Condition;
import io.pddl.sqlparser.bean.ConditionContext;
import io.pddl.sqlparser.bean.Table;

public abstract class AbstractRouterSupport{
	
	protected Log logger = LogFactory.getLog(getClass());

	protected ShardingCache shardingCache;
	
	public void setShardingCache(ShardingCache shardingCache){
		this.shardingCache= shardingCache;
	}

	/**
	 * 获取解析过的逻辑表集合
	 * 逻辑SQL：select ... from t_item i,t_order o,t_category c where ...
	 * 其中t_item、t_order和t_category都是逻辑表，并且t_item是t_order逻辑子表
	 * 解析的结果[[t_order,t_item],[t_category]]
	 * 
	 * @param ctx 执行上下文
	 * @return List<List<LogicTable>>
	 */
	protected List<List<LogicTable>> parseLogicTables(ExecuteContext ctx) {
		@SuppressWarnings("unchecked")
		//从ThreadLocal中获取已经解析过的逻辑表结果集
		List<List<LogicTable>> result= (List<List<LogicTable>>)ExecuteHolder.getAttribute("tableRouter.parseLogicTables");
		if(result!= null){
			if(logger.isInfoEnabled()){
				logger.info("get logicTables from ThreadLocal: {tableRouter.parseLogicTables="+result+"}");
			}
			return result;
		}
		//获取SQL中所有的表名
		Set<Table> tables= ctx.getSQLParsedResult().getTables();
		Map<String, List<LogicTable>> hash = new HashMap<String, List<LogicTable>>(tables.size());
		for (Table table: tables) {
			String tableName= table.getName();
			LogicTable logicTable = ctx.getLogicTableRepository().getLogicTable(tableName);
			//如果不是逻辑表，不需要处理
			if (logicTable == null) {
				if(logger.isInfoEnabled()){
					logger.info("table ["+tableName+"] is not logic table");
				}
				continue;
			}
			//获取逻辑主表的标识，具有相同的标识放在同一个列表中
			String key = logicTable.getLayerIdx().split(",")[0];
			List<LogicTable> list = hash.get(key);
			if (list == null) {
				hash.put(key, list= new ArrayList<LogicTable>());
			}
			list.add(logicTable);
		}
		if(logger.isInfoEnabled()){
			logger.info("classify Logic table: "+hash);
		}
		result = new ArrayList<List<LogicTable>>(hash.size());
		//对相同逻辑主表的集合做排序，层次越深位置越靠后
		for (List<LogicTable> list: hash.values()) {
			Collections.sort(list, new Comparator<LogicTable>() {
				@Override
				public int compare(LogicTable o1, LogicTable o2) {
					String[] idx1 = o1.getLayerIdx().split(",");
					String[] idx2 = o2.getLayerIdx().split(",");
					int result = idx1.length - idx2.length;
					if (result == 0) {
						for (int i = 0; i < idx1.length; i++) {
							if (0 != (result = Integer.parseInt(idx1[i]) - Integer.parseInt(idx2[i]))) {
								return result;
							}
						}
					}
					return result;
				}
			});
			result.add(list);
		}
		//缓存解析逻辑表结果集到ThreadLocal中
		ExecuteHolder.setAttribute("tableRouter.parseLogicTables", result);
		if(logger.isInfoEnabled()){
			logger.info("put logicTables to ThreadLocal: "+"{tableRouter.parseLogicTables="+result+"}");
		}
		return result;
	}
	
	/**
	 * 根据逻辑表名和路由列集合获取路由值集合
	 * @param ctx 执行上下文
	 * @param logicTableName 逻辑表名
	 * @param columns 路由列名集合
	 * @return List<List<ShardingValue<?>>>
	 */
	protected List<List<ShardingValue<?>>> getShardingValues(ExecuteContext ctx,String logicTableName,List<String> columns){
		//获取多个or关联的ConditionContext对象里面的Condition是and关系，如下
		//ConditionContext(Condition and Condition) or ConditionContext(Condition and Condition)
		List<ConditionContext> conditionContexts= ctx.getSQLParsedResult().getConditions();
		List<List<ShardingValue<?>>> result= new ArrayList<>(conditionContexts.size());
loop:	for(ConditionContext conditionContext : conditionContexts){
			List<ShardingValue<?>> shardingValues = new ArrayList<ShardingValue<?>>(columns.size());
			for (String column : columns) {
				Condition condition = getCondition(conditionContext,logicTableName, column);
				if (condition != null) {
					if(logger.isInfoEnabled()){
						logger.info("condition found: "+condition);
					}
					//仅仅支持=、in和between等操作
					switch(condition.getOperator()){
						case EQUAL:
						case IN:
							if(condition.getValues().size()==1){
								shardingValues.add(new ShardingSingleValue<Comparable<?>>(column, condition.getValues()));
							}
							else{
								shardingValues.add(new ShardingCollectionValue<Comparable<?>>(column, condition.getValues()));
							}
							break;
						case BETWEEN:
							shardingValues.add(new ShardingRangeValue<Comparable<?>>(column, condition.getValues()));
							break;
						default:
							logger.warn("column ["+column+"] not support operation: "+condition.getOperator());
							continue loop;
					}
				}
				else{
					if(logger.isInfoEnabled()){
						logger.info("condition not found in "+conditionContext+" for {table="+logicTableName+",column="+column+"}");
					}
					continue loop;
				}
			}
			if(logger.isInfoEnabled()){
				logger.info("one sharding value found: "+shardingValues);
			}
			result.add(shardingValues);
		}
		return result;
	}
	
	/**
	 * 根据表名和列名获取查询条件
	 * SQL语句：select ... from t_order o,t_item i where o.order_id= i.order_id and o.user_id=4
	 * getCondition(ctx,"t_order","user_id") 可以获取到对应的条件值{column={tableName=t_order,columnName=user_id},operator="=",values=[4]}
	 * getCondition(ctx,"t_item","user_id") 获取不到条件值，因为SQL语句中表t_item和列user_id没有关联关系
	 * 
	 * @param ConditionContext 条件上下文
	 * @param tableName 表名
	 * @param column 列表
	 * @return Condition
	 */
	protected Condition getCondition(ConditionContext conditionContext,String tableName, String column) {
		Optional<Condition> option= conditionContext.find(tableName, column);
		return option.isPresent()? option.get(): null;
	}
	
	protected Condition getCondition(ExecuteContext ctx,String tableName, String column) {
		return getCondition(ctx.getSQLParsedResult().getConditions().get(0),tableName,column);
	}
	
}
