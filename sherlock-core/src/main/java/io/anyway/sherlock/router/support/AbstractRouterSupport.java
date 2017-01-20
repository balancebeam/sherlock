package io.anyway.sherlock.router.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.anyway.sherlock.router.strategy.ShardingStrategy;
import io.anyway.sherlock.router.strategy.value.ShardingCollectionValue;
import io.anyway.sherlock.router.strategy.value.ShardingRangeValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Optional;

import io.anyway.sherlock.cache.ShardingCache;
import io.anyway.sherlock.executor.ExecuteContext;
import io.anyway.sherlock.executor.ExecuteHolder;
import io.anyway.sherlock.router.strategy.value.ShardingSingleValue;
import io.anyway.sherlock.router.strategy.value.ShardingValue;
import io.anyway.sherlock.router.table.LogicTable;
import io.anyway.sherlock.sqlparser.bean.Condition;
import io.anyway.sherlock.sqlparser.bean.ConditionContext;
import io.anyway.sherlock.sqlparser.bean.Table;

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
		if(result != null && !result.isEmpty()){
			if(logger.isInfoEnabled()){
				logger.info("get logicTables from ThreadLocal: {tableRouter.parseLogicTables="+result+"}");
			}
			return result;
		}
		//获取SQL中所有的表名
		Set<Table> tables= ctx.getSQLParsedResult().getTables();
		Set<String> idx= new HashSet<String>(tables.size());
		for (Table table: tables) {
			String tableName= table.getName();
			LogicTable logicTable = ctx.getLogicTableRepository().getLogicTable(tableName);
			//如果不是逻辑表，不需要处理
			if (logicTable == null) {
				if(logger.isDebugEnabled()){
					logger.debug("table ["+tableName+"] is not logic table");
				}
				continue;
			}
			idx.add(tableName);
		}
		//逻辑子表的父表若在，则不搜集此表，并且按逻辑主表维度归类
		Map<String, List<LogicTable>> hash = new HashMap<String, List<LogicTable>>(tables.size());
		loop: for(final String each: idx){
			String tableName= each;
			final LogicTable logicTable= ctx.getLogicTableRepository().getLogicTable(tableName);
			for(LogicTable table= logicTable;(table=table.getParent())!= null;){
				tableName= table.getName();
				if(idx.contains(tableName)){
					if(logger.isDebugEnabled()){
						logger.debug("logic table "+ each+ " 's parent table "+ tableName+ " is exists.");
					}
					continue loop;
				}
			}
			//获取逻辑主表的标识，具有相同的标识放在同一个列表中
			List<LogicTable> l= hash.get(tableName);
			if (l == null) {
				hash.put(tableName, l= new ArrayList<LogicTable>());
			}
			l.add(logicTable);
		}
		
		result = new ArrayList<List<LogicTable>>(hash.values());
		//缓存解析逻辑表结果集到ThreadLocal中
		ExecuteHolder.setAttribute("tableRouter.parseLogicTables", result);
		if(logger.isDebugEnabled()){
			logger.debug("put logicTables to ThreadLocal: "+"{tableRouter.parseLogicTables="+result+"}");
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
		List<List<ShardingValue<?>>> result= new ArrayList<List<ShardingValue<?>>>(conditionContexts.size());
		loop: for(ConditionContext conditionContext : conditionContexts){
			List<ShardingValue<?>> shardingValues = new ArrayList<ShardingValue<?>>(columns.size());
			for (String column : columns) {
				Condition condition = getCondition(conditionContext,logicTableName, column);
				if (condition != null) {
					if(logger.isDebugEnabled()){
						logger.debug("condition found: "+condition);
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
					if(logger.isDebugEnabled()){
						logger.debug("condition not found in "+conditionContext+" for {table="+logicTableName+",column="+column+"}");
					}
					continue loop;
				}
			}
			if(logger.isDebugEnabled()){
				logger.debug("one sharding value found: "+shardingValues);
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
	 * @param conditionContext 条件上下文
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
	
	protected Collection<String> doSharding(
			ShardingStrategy strategy,
			ExecuteContext ctx,
			Collection<String> availableNames,
			List<List<ShardingValue<?>>> values){
		Set<String> result= new HashSet<String>();
		//多条记录间是or的关系，路由集合是合集操作
		for(List<ShardingValue<?>> shardingValues: values){
			result.addAll(strategy.doSharding(ctx, availableNames, shardingValues));
		}
		return result;
	}
}
