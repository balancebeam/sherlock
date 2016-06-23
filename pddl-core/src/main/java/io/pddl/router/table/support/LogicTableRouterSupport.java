package io.pddl.router.table.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.springframework.util.CollectionUtils;

import io.pddl.exception.ShardingTableException;
import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteHolder;
import io.pddl.router.strategy.config.ShardingStrategyConfig;
import io.pddl.router.strategy.value.ShardingValue;
import io.pddl.router.support.AbstractRouterSupport;
import io.pddl.router.table.LogicChildTable;
import io.pddl.router.table.LogicTable;
import io.pddl.router.table.LogicTableRouter;
import io.pddl.sqlparser.SQLBuilder;
import io.pddl.sqlparser.bean.Condition;
import io.pddl.sqlparser.bean.SQLStatementType;

/**
 * 分表路由实现
 * @author yangzz
 *
 */
public class LogicTableRouterSupport extends AbstractRouterSupport implements LogicTableRouter {

	@Override
	public Collection<String> doRoute(ExecuteContext ctx,String dataSourceName) {
		List<List<LogicTable>> logicTables= parseLogicTables(ctx);
		if(CollectionUtils.isEmpty(logicTables)){
			if(logger.isInfoEnabled()){
				logger.info("no logic table for sql :"+ ctx.getLogicSql());
			}
			return Collections.singleton(ctx.getLogicSql());
		}
		return doMultiLogicTableSharding(ctx,dataSourceName,logicTables);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> doMultiLogicTableSharding(ExecuteContext ctx,String dataSourceName,List<List<LogicTable>> logicTables) {
		List<String> result;
		if(SQLStatementType.INSERT!= ctx.getStatementType() &&
			null!= (result= (List<String>)ExecuteHolder.getAttribute("tableRouter.doMultiLogicTableSharding"))){
			if(logger.isInfoEnabled()){
				logger.info("will use threadlocal sharding result: "+result);
			}
			return result;
		}
		List<Set<String>> postfixes = new ArrayList<Set<String>>();
		for (List<LogicTable> tables : logicTables) {
			Set<String> pfixes = new HashSet<String>();
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
				Collection<String> ps=doSingleLogicTableSharding(ctx,dataSourceName,logicTable);
				if(logger.isInfoEnabled()){
					logger.info("table ["+ logicTable.getName()+"] candidate postfix names: "+ps);
				}
				pfixes.addAll(ps);
			}
			postfixes.add(pfixes);
		}
		logger.info("logic table and postfixes "+logicTables+","+postfixes);
		result = new ArrayList<String>();
		makeupCartesian(ctx,logicTables, postfixes, 0, new HashMap<String,String>(), result);
		//cache the result
		if(SQLStatementType.INSERT!= ctx.getStatementType()){
			if(logger.isInfoEnabled()){
				logger.info("bind sharding result to threadlocal: "+result);
			}
			ExecuteHolder.setAttribute("tableRouter.doMultiLogicTableSharding",result);
		}
		return result;
	}
	
	private Collection<String> doSingleLogicTableSharding(ExecuteContext ctx,String dataSourceName,LogicTable logicTable) {
		ShardingStrategyConfig strategyConfig = logicTable.getTableStrategyConfig();
		List<List<ShardingValue<?>>> shardingValues = getShardingValues(ctx,logicTable.getName(),strategyConfig.getColumns());
		if (shardingValues.isEmpty() && SQLStatementType.INSERT== ctx.getStatementType()) {
			//if insert child table when it's condition not found
			if(logger.isInfoEnabled()){
				logger.info("sharding value is empty and sql type is insert");
			}
			return Collections.singletonList(lookupSingleTablePostfix(ctx,dataSourceName,logicTable));
		}
		return strategyConfig.getStrategy().doSharding(ctx,logicTable.getTablePostfixes(), shardingValues);
	}
	
	//get postfix from parent table
	private String lookupSingleTablePostfix(ExecuteContext ctx,String dataSourceName,LogicTable logicTable){
		if(logicTable.isChildTable()){
			//find parent logic table primary key value
			Condition primaryKeyCondition= getCondition(ctx,logicTable.getName(),logicTable.getPrimaryKey());
			if(primaryKeyCondition== null){
				throw new IllegalArgumentException("miss primary-key value for table "+logicTable.getName());
			}
			
			String foreignKey= ((LogicChildTable)logicTable).getForeignKey();
			if(logger.isInfoEnabled()){
				logger.info("table ["+ logicTable.getName()+"] foreign key: "+foreignKey);
			}
			Condition foreignKeyCondition= getCondition(ctx,logicTable.getName(),foreignKey);
			if(foreignKeyCondition!= null){
				if(logger.isInfoEnabled()){
					logger.info("foreign key condition: "+foreignKeyCondition);
				}
				LogicTable parent= logicTable.getParent();
				String primaryKey= parent.getPrimaryKey();
				if(shardingCache!= null){
					if(logger.isInfoEnabled()){
						logger.info("will get table postfix from cache");
					}
					Comparable<?> value= foreignKeyCondition.getValues().get(0);
					String postfix= shardingCache.getLogicTablePostfix(parent.getName(),parent.getPrimaryKey(),value);
					if(postfix!= null){
						value= primaryKeyCondition.getValues().get(0);
						shardingCache.putLocalTablePostfix(logicTable.getName(), logicTable.getPrimaryKey(), value, postfix);
						if(logger.isInfoEnabled()){
							logger.info("found table postfix from cache: "+postfix);
						}
						return postfix;
					}
				}
				
				try {
					PreparedStatement ps= null;
					ResultSet rs= null;
					if(logger.isInfoEnabled()){
						logger.info("will select all parent table: "+parent.getName());
					}
					for(String postfix: parent.getTablePostfixes()){
						StringBuilder builder= new StringBuilder();
						builder.append("select t.")
								.append(primaryKey)
								.append(" from ")
								.append(parent.getName())
								.append(postfix)
								.append(" t where t.")
								.append(primaryKey)
								.append("=?");
						String sql= builder.toString();
						try{
							Connection conn= ctx.getShardingConnection().getConnection(dataSourceName);
							ps= conn.prepareStatement(sql);
							ps.setObject(1, foreignKeyCondition.getValues().get(0));
							rs= ps.executeQuery();
							if(rs.next()){
								if(logger.isInfoEnabled()){
									logger.info("query parent table: " +sql);
									logger.info("found postfix: "+postfix);
								}
								if(shardingCache!= null){
									Comparable<?> value= primaryKeyCondition.getValues().get(0);
									shardingCache.putLocalTablePostfix(logicTable.getName(), logicTable.getPrimaryKey(), value, postfix);
									if(logger.isInfoEnabled()){
										logger.info("put table postfix into cache: " +postfix);
									}
								}
								return postfix;
							}
						}finally{
							DbUtils.closeQuietly(rs);
							DbUtils.closeQuietly(ps);
						}
					}
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		throw new ShardingTableException("can't locate which insert table "+logicTable.getName());
	}
	/*
	 * 多表间笛卡尔集
	 * 如：logicTables= [[t_order,t_item],[t_category]] ,postfixes= [[_0,_1],[_1,_2]]
	 * 结果为[t_order_0,t_item_0,t_category_1]
	 * 		[t_order_0,t_item_0,t_category_2]
	 * 		[t_order_1,t_item_1,t_category_1]
	 * 		[t_order_1,t_item_1,t_category_2]
	 */
	private void makeupCartesian(
			ExecuteContext ctx,
			List<List<LogicTable>> logicTables, 
			List<Set<String>> postfixes,
			int index, 
			Map<String,String> hash, 
			List<String> result) {
		if (logicTables.size() <= index) {
			SQLBuilder sqlBuilder= ctx.getSQLParsedResult().getSqlBuilder();
			for (Entry<String,String> entry: hash.entrySet()) {
				String logicTable= entry.getKey();
				String actualTable= entry.getValue();
				sqlBuilder.buildSQL(logicTable, actualTable);
			}
			String actualSql= sqlBuilder.toSQL();
			if(logger.isInfoEnabled()){
				logger.info("actual sql: "+actualSql);
			}
			result.add(actualSql);
			return;
		}
		for (String postfix: postfixes.get(index)) {
			for (LogicTable table : logicTables.get(index)) {
				hash.put(table.getName(), table.getName() + postfix);
			}
			makeupCartesian(ctx,logicTables, postfixes, index + 1, hash, result);
		}
	}
}
