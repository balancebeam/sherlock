package io.pddl.router.table;

public interface GlobalTableRepository {

	/**
	 * 是不是全局表或字典表，当对全局表进行DML操作时，需要应用到所有数据源上
	 * <pddl:tables>
	 * 	<pddl:global-table name="stock"/>
	 * 	<pddl:global-table name="city"/>
	 * 	...
	 * </pddl:tables>
	 * 
	 * @param name 表明
	 * @return true | false
	 */
	boolean isGlobalTable(String name);
	
	/**
	 * 全局表定义是否为空
	 * @return
	 */
	boolean isEmpty();
}
