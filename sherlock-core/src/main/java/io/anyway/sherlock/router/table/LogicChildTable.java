package io.anyway.sherlock.router.table;

public interface LogicChildTable extends LogicTable{

	/**
	 * 获取外键值，此外键为父表的主键
	 * @return
	 */
	String getForeignKey();
}
