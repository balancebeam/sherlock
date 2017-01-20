package io.anyway.sherlock.router.table;

import java.util.Collection;

import io.anyway.sherlock.executor.ExecuteContext;

/**
 * 表路由解析器
 * @author yangzz
 * 逻辑SQL：select order_id,order_name,order_status from t_order where order_id=3 and user_id=1
 * 实际SQL：select order_id,order_name,order_status from t_order_1 where order_id=3 and user_id=1
 *
 */
public interface LogicTableRouter {

	/**
	 * 路由表
	 * @param ctx 上下文
	 * @param dataSourceName 数据源名称
	 * @return Collection<String>
	 */
	Collection<String> doRoute(ExecuteContext ctx,String dataSourceName);
}
