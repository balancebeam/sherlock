package io.pddl.router.table;

import java.util.List;

import io.pddl.router.strategy.config.ShardingStrategyConfig;
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
	
	/**
	 * 获取逻辑表的层级中的唯一标识
	 * <pddl:tables>
	 * 	<pddl:logic-table name="t_order" ...> layerIdx=0
	 * 		<pddl:logic-child-table name="t_item" ...> layerIdx=0,0
	 * 			<pddl:logic-child-table name="t_item_ext" ...> layerIdx=0,0,0
	 * 			<pddl:logic-child-table name="t_item_sub" ...> layerIdx=0,0,1
	 * 		</pddl:logic-child-table>
	 * 		<pddl:logic-child-table name="t_mistake" .../> layerIdx=0,1
	 * 	</pddl:logic-table>
	 * 	<pddl:logic-table name="t_category" .../> layerIdx=1
	 * 	<pddl:logic-table name="t_product" .../> layerIdx=2
	 * </pddl:tables>
	 * 
	 * @return String
	 */
	String getLayerIdx();
}
