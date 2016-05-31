package io.pddl.table.router;

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
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.cobar.client.sqlparser.SQLParseEngine;
import com.alibaba.cobar.client.sqlparser.SQLParsedResult;
import com.alibaba.cobar.client.sqlparser.SQLParserFactory;
import com.alibaba.cobar.client.sqlparser.bean.DatabaseType;
import com.alibaba.druid.stat.TableStat.Condition;

import io.pddl.table.LogicChildTable;
import io.pddl.table.LogicTable;
import io.pddl.table.LogicTableRepository;
import io.pddl.table.LogicTableRouter;
import io.pddl.table.ShardingValue;
import io.pddl.table.exception.ShardingTableException;
import io.pddl.table.model.LogicTableStrategyConfig;
import io.pddl.table.model.ShardingCollectionValue;
import io.pddl.table.model.ShardingRangeValue;
import io.pddl.table.model.ShardingSingleValue;

public class DefaultLogicTableRouter implements LogicTableRouter {
	
	private Logger logger = LoggerFactory.getLogger(DefaultLogicTableRouter.class);

	private LogicTableRepository logicTableRepository;

	public void setLogicTableRepository(LogicTableRepository logicTableRepository) {
		this.logicTableRepository = logicTableRepository;
	}

	@Override
	public Collection<String> doRoute(String sql, Object[] parameters) {
		if (logicTableRepository.isLogicTableEmpty()) {
			return Collections.singleton(sql);
		}
		
		SQLParseEngine sqlParseEngine= SQLParserFactory.create(DatabaseType.POSTGRESQL, sql, Arrays.asList(parameters));
		SQLParsedResult sqlParsedResult = sqlParseEngine.parse();
		return null;
	}

	private List<Map<String, String>> doSharding(List<String> logicTableNames) {
		List<List<LogicTable>> cascadeTables = parseCascadeTables(logicTableNames);
		List<Set<String>> cascadePostfixes = new ArrayList<Set<String>>();
		for (List<LogicTable> list : cascadeTables) {
			Set<String> postfixes = new HashSet<String>();
			for (int i = 0; i < list.size(); i++) {
				LogicTable logicTable = list.get(i);
				String hierarchical = logicTable.getHierarchical();
				tables: for (int j = 0; j < i; j++) {
					if (hierarchical.startsWith(list.get(j).getHierarchical())) {
						continue tables;
					}
				}
				postfixes.addAll(doLogicTableSharding(logicTable));
			}
			cascadePostfixes.add(postfixes);
		}
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		doCartesianTables(cascadeTables, cascadePostfixes, 0, new HashMap<String, String>(), result);
		return result;
	}

	private List<List<LogicTable>> parseCascadeTables(List<String> logicTableNames) {
		Map<String, List<LogicTable>> hash = new HashMap<String, List<LogicTable>>(logicTableNames.size());
		for (String tableName : logicTableNames) {
			LogicTable logicTable = logicTableRepository.getLogicTable(tableName);
			if (logicTable == null) {
				continue;
			}
			String key = logicTable.getHierarchical().split(",")[0];
			List<LogicTable> list = hash.get(key);
			if (list == null) {
				hash.put(key, list = new ArrayList<LogicTable>());
			}
			list.add(logicTable);
		}
		List<List<LogicTable>> result = new ArrayList<List<LogicTable>>();
		for (Iterator<List<LogicTable>> it = hash.values().iterator(); it.hasNext();) {
			List<LogicTable> list = it.next();
			Collections.sort(list, new Comparator<LogicTable>() {
				@Override
				public int compare(LogicTable o1, LogicTable o2) {
					String[] hierar1 = o1.getHierarchical().split(",");
					String[] hierar2 = o1.getHierarchical().split(",");
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

	private Collection<String> doLogicTableSharding(LogicTable logicTable) {
		LogicTableStrategyConfig strategyConfig = logicTable.getStrategyConfig();
		List<String> columns = strategyConfig.getColumns();
		List<ShardingValue<?>> shardingValues = new ArrayList<ShardingValue<?>>(columns.size());
		for (String column : columns) {
			Condition condition = getCondition(logicTable.getName(), column);
			if (condition != null) {
				if ("=".equals(condition.getOperator())) {
					shardingValues.add(new ShardingSingleValue(column, condition.getValues()));
				} else if ("in".equals(condition.getOperator())) {
					shardingValues.add(new ShardingCollectionValue(column, condition.getValues()));
				} else if ("between".equals(condition.getOperator())) {
					shardingValues.add(new ShardingRangeValue(column, condition.getValues()));
				} else {
					throw new UnsupportedOperationException(condition.getOperator());
				}
			}
		}
//		if ("INSERT" && shardingValues.isEmpty()) {
//			return Collections.singletonList(lookupInsertTablePostfix(logicTable,conn));
//		}
		return strategyConfig.getStrategy().doSharding(logicTable, shardingValues);
	}
	
	private void doCartesianTables(
			List<List<LogicTable>> cascadeTables, 
			List<Set<String>> cartesianPostfixes,
			int index, 
			Map<String, String> hash, 
			List<Map<String, String>> result) {
		if (cascadeTables.size() <= index) {
			result.add(new HashMap<String, String>(hash));
			return;
		}
		Set<String> postfixes = cartesianPostfixes.get(index);
		for (Iterator<String> it = postfixes.iterator(); it.hasNext();) {
			String postfix = it.next();
			for (LogicTable table : cascadeTables.get(index)) {
				hash.put(table.getName(), table.getName() + postfix);
			}
			doCartesianTables(cascadeTables, cartesianPostfixes, index + 1, hash, result);
		}
	}

	private Condition getCondition(String tableName, String column) {
		return null;
	}
	
	private String lookupInsertTablePostfix(LogicTable logicTable,Connection conn){
		if(!logicTable.isPrimaryTable()){
			String foreignKey= ((LogicChildTable)logicTable).getForeignKey();
			Condition condition= getCondition(logicTable.getName(),foreignKey);
			if(condition!= null){
				LogicTable parent= logicTable.getParent();
				String primaryKey= parent.getPrimaryKey();
				try {
					PreparedStatement ps= null;
					ResultSet rs= null;
					for(String postfix: parent.getPostfixes()){
						String sql= "select "+primaryKey+" from "+ parent.getName()+ postfix + " where "+ primaryKey + "=?";
						try{
							ps= conn.prepareStatement(sql);
							ps.setObject(1, condition.getValues().get(0));
							rs= ps.executeQuery();
							if(rs.next()){
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

}
