package io.anyway.sherlock.router.table;

import java.util.List;

public interface GlobalTableRepository {

	/**
	 * 是不是全局表或字典表，当对全局表进行DML操作时，需要应用到所有数据源上
	 * <pddl:tables>
	 * 	<pddl:global-table name="stock"/>
	 * 	<pddl:global-table name="city"/>
	 * 	...
	 * </pddl:tables>
	 * 
	 * @param name 表名
	 * @return true | false
	 */
	boolean isGlobalTable(String name);

	/**
	 * 获取全局表的分片数据原名称集合
	 * @param name 表名
	 * @return List<String>
     */
	List<String> getPartitionDataSourceNames(String name);
	
	/**
	 * 全局表定义是否为空
	 * @return
	 */
	boolean isEmpty();
}
