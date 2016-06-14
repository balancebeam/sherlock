package io.pddl.router.table;

import java.util.Collection;

public interface LogicTableRepository {

	/**
	 * 是否是逻辑字表
	 * @param tableName 表名
	 * @return true | false
	 */
	boolean isLogicChildTable(String tableName);
	
	/**
	 * 获取逻辑表
	 * @param tableName 表名
	 * @return LogicTable
	 */
	LogicTable getLogicTable(String tableName);
	
	/**
	 * 获取所有逻辑表名
	 * @return
	 */
	Collection<String> getLogicTableNames();
}
