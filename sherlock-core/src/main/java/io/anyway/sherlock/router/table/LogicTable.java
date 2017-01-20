package io.anyway.sherlock.router.table;

import java.util.List;

import io.anyway.sherlock.router.strategy.config.ShardingStrategyConfig;

/**
 * 逻辑表定义
 * @author yangzz
 *
 */
public interface LogicTable {
	
	/**
	 * 获取逻辑表名
	 * @return String
	 */
	String getName();

	/**
	 * 获取逻辑表所属分片数据源名称集合
	 * @return
     */
	List<String> getPartitionDataSourceNames();
	
	/**
	 * 获取主键名称
	 * @return String
	 */
	String getPrimaryKey();
	
	/**
	 * 获取表的后缀名称集合
	 * 如：[_0 | _1 | _2]
	 * @return List<String>
	 */
	List<String> getTablePostfixes();
	
	/**
	 * 获取逻辑表的父表
	 * @return LogicTable
	 */
	LogicTable getParent();
	
	/**
	 * 获取直接逻辑子表集合
	 * @return List<? extends LogicTable>
	 */
	List<? extends LogicTable> getChildren();
	
	/**
	 * 获取路由表规则
	 * @return ShardingStrategyConfig
	 */
	ShardingStrategyConfig getTableStrategyConfig();
	
	/**
	 * 获取路由数据源规则
	 * @return ShardingStrategyConfig
	 */
	ShardingStrategyConfig getDataSourceStrategyConfig();
	
	/**
	 * 是否是逻辑子表
	 * @return true | false
	 */
	boolean isChildTable();
	
}
