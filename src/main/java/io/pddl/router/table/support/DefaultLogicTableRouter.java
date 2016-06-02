package io.pddl.router.table.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import io.pddl.cache.ShardingCache;
import io.pddl.exception.ShardingTableException;
import io.pddl.executor.ExecutorContextHolder;
import io.pddl.executor.support.ExecutorContextSupport;
import io.pddl.router.table.LogicChildTable;
import io.pddl.router.table.LogicTable;
import io.pddl.router.table.LogicTableRepository;
import io.pddl.router.table.LogicTableRouter;
import io.pddl.router.table.config.LogicTableStrategyConfig;
import io.pddl.router.table.value.ShardingCollectionValue;
import io.pddl.router.table.value.ShardingRangeValue;
import io.pddl.router.table.value.ShardingSingleValue;
import io.pddl.router.table.value.ShardingValue;
import io.pddl.sqlparser.SQLBuilder;
import io.pddl.sqlparser.SQLParseEngine;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.SQLParserFactory;
import io.pddl.sqlparser.bean.Condition;
import io.pddl.sqlparser.bean.SQLStatementType;
import io.pddl.sqlparser.bean.Table;
import io.pddl.util.CollectionUtils;

public class DefaultLogicTableRouter implements LogicTableRouter {
	
	private Logger logger = LoggerFactory.getLogger(DefaultLogicTableRouter.class);

	private LogicTableRepository logicTableRepository;
	
	private ShardingCache shardingCache;

	public void setLogicTableRepository(LogicTableRepository logicTableRepository) {
		this.logicTableRepository = logicTableRepository;
	}
	
	public void setShardingCache(ShardingCache shardingCache){
		this.shardingCache= shardingCache;
	}

	@Override
	public Collection<String> doRoute(String sql, Object[] parameters,Connection conn) {
		if (logicTableRepository.isLogicTableEmpty()) {
			return Collections.singleton(sql);
		}
		
		SQLParseEngine sqlParseEngine= SQLParserFactory.create(sql, Arrays.asList(parameters));
		SQLParsedResult sqlParsedResult = sqlParseEngine.parse();
		((ExecutorContextSupport)ExecutorContextHolder.getContext()).setSQLParsedResult(sqlParsedResult);
		List<List<LogicTable>> logicTables= parseLogicTables(sqlParsedResult.getTables());
		if(CollectionUtils.isEmpty(logicTables)){
			return Collections.singleton(sql);
		}
		return doMultilpleLogicTableSharding(logicTables,conn);
	}
	
	private List<List<LogicTable>> parseLogicTables(Set<Table> tables) {
		Map<String, List<LogicTable>> hash = new HashMap<String, List<LogicTable>>(tables.size());
		for (Iterator<Table> it= tables.iterator();it.hasNext();) {
			String tableName= it.next().getName();
			LogicTable logicTable = logicTableRepository.getLogicTable(tableName);
			//if not logic table
			if (logicTable == null) {
				continue;
			}
			String partition= ExecutorContextHolder.getContext().getPartitionDataSource().getName();
			//if not match given database partition
			if(!logicTable.matchPartition(partition)){
				continue;
			}
			String key = logicTable.getHierarchical().split(",")[0];
			List<LogicTable> list = hash.get(key);
			if (list == null) {
				hash.put(key, list= new ArrayList<LogicTable>());
			}
			list.add(logicTable);
		}
		List<List<LogicTable>> result = new ArrayList<List<LogicTable>>(hash.size());
		for (Iterator<List<LogicTable>> it = hash.values().iterator(); it.hasNext();) {
			List<LogicTable> list = it.next();
			Collections.sort(list, new Comparator<LogicTable>() {
				@Override
				public int compare(LogicTable o1, LogicTable o2) {
					String[] hierar1 = o1.getHierarchical().split(",");
					String[] hierar2 = o2.getHierarchical().split(",");
					int result = hierar1.length - hierar2.length;
					if (result == 0) {
						for (int i = 0; i < hierar1.length; i++) {
							if (0 != (result = Integer.parseInt(hierar1[i]) - Integer.parseInt(hierar2[i]))) {
								return result;
							}
						}
					}
					return result;
				}
			});
			result.add(list);
		}
		return result;
	}

	private List<String> doMultilpleLogicTableSharding(List<List<LogicTable>> logicTables,Connection conn) {
		List<Set<String>> postfixes = new ArrayList<Set<String>>();
		for (List<LogicTable> tables : logicTables) {
			Set<String> pfixes = new HashSet<String>();
tables: 	for (int i = 0; i < tables.size(); i++) {
				LogicTable logicTable = tables.get(i);
				String hierarchical = logicTable.getHierarchical();
				for (int j = 0; j < i; j++) {
					if (hierarchical.startsWith(tables.get(j).getHierarchical())) {
						continue tables;
					}
				}
				pfixes.addAll(doSingleLogicTableSharding(logicTable,conn));
			}
			postfixes.add(pfixes);
		}
		List<String> result = new ArrayList<String>();
		makeupCartesian(logicTables, postfixes, 0, new HashMap<String,String>(), result);
		return result;
	}
	
	private Collection<String> doSingleLogicTableSharding(LogicTable logicTable,Connection conn) {
		LogicTableStrategyConfig strategyConfig = logicTable.getStrategyConfig();
		List<String> columns = strategyConfig.getColumns();
		List<ShardingValue<?>> shardingValues = new ArrayList<ShardingValue<?>>(columns.size());
		for (String column : columns) {
			Condition condition = getCondition(logicTable.getName(), column);
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
		if (shardingValues.isEmpty() && SQLStatementType.INSERT== ExecutorContextHolder.getContext().getSQLParsedResult().getStatementType()) {
			//if insert child table when it's condition not found
			return Collections.singletonList(lookupSingleTablePostfix(logicTable,conn));
		}
		return strategyConfig.getStrategy().doSharding(logicTable, shardingValues);
	}
	
	//find Condition by tablename and column
	private Condition getCondition(String tableName, String column) {
		SQLParsedResult sqlParsedResult= ExecutorContextHolder.getContext().getSQLParsedResult();
		Optional<Condition> option= sqlParsedResult.getCondition().find(tableName, column);
		return option.isPresent()? option.get(): null;
	}
	//get postfix from parent table
	private String lookupSingleTablePostfix(LogicTable logicTable,Connection conn){
		if(!logicTable.isPrimaryTable()){
			//find parent logic table primary key value
			Condition primaryCondtion= getCondition(logicTable.getName(),logicTable.getPrimaryKey());
			if(primaryCondtion== null){
				throw new IllegalArgumentException("miss primary-key value for table "+logicTable.getName());
			}
			
			String foreignKey= ((LogicChildTable)logicTable).getForeignKey();
			Condition foreignCondition= getCondition(logicTable.getName(),foreignKey);
			if(foreignCondition!= null){
				LogicTable parent= logicTable.getParent();
				String primaryKey= parent.getPrimaryKey();
				if(shardingCache!= null){
					String postfix= shardingCache.getLogicTablePostfix(parent.getName(),parent.getPrimaryKey(),foreignCondition.getValues());
					if(postfix!= null){
						shardingCache.putLocalTablePostfix(logicTable.getName(), logicTable.getPrimaryKey(), primaryCondtion.getValues(), postfix);
						return postfix;
					}
				}
				
				try {
					PreparedStatement ps= null;
					ResultSet rs= null;
					for(String postfix: parent.getPostfixes()){
						String sql= "select t."+primaryKey+" from "+ parent.getName()+ postfix + " t where t."+ primaryKey + "=?";
						try{
							ps= conn.prepareStatement(sql);
							ps.setObject(1, foreignCondition.getValues().get(0));
							rs= ps.executeQuery();
							if(rs.next()){
								if(shardingCache!= null){
									shardingCache.putLocalTablePostfix(logicTable.getName(), logicTable.getPrimaryKey(), primaryCondtion.getValues(), postfix);
								}
								return postfix;
							}
						}finally{
							DbUtils.closeQuietly(rs);
							DbUtils.closeQuietly(ps);
						}
					}
				} catch (SQLException e) {
					logger.equals(e);
				}
			}
		}
		throw new ShardingTableException("can't locate which insert table "+logicTable.getName());
	}
	//table cartesian
	private void makeupCartesian(
			List<List<LogicTable>> logicTables, 
			List<Set<String>> postfixes,
			int index, 
			Map<String,String> ctx, 
			List<String> result) {
		if (logicTables.size() <= index) {
			SQLBuilder sqlBuilder= ExecutorContextHolder.getContext().getSQLParsedResult().getSqlBuilder();
			for(Iterator<Entry<String,String>> it= ctx.entrySet().iterator();it.hasNext();){
				Entry<String,String> entry= it.next();
				String logicTable= entry.getKey();
				String actualTable= entry.getValue();
				sqlBuilder.buildSQL(logicTable, actualTable);
			}
			result.add(sqlBuilder.toSQL());
			return;
		}
		for (Iterator<String> it = postfixes.get(index).iterator(); it.hasNext();) {
			String postfix = it.next();
			for (LogicTable table : logicTables.get(index)) {
				ctx.put(table.getName(), table.getName() + postfix);
			}
			makeupCartesian(logicTables, postfixes, index + 1, ctx, result);
		}
	}
}
