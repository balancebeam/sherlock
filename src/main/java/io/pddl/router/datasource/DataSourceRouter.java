package io.pddl.router.datasource;

import java.util.Collection;

import io.pddl.executor.ExecuteContext;

/**
 * 数据源路由解析引擎
 * @author yangzz
 *
 */
public interface DataSourceRouter {
	
	/**
	 * 路由数据源操作
	 * @param ctx ExecuteContext SQL语句执行上下文
	 * @return Collection<String>
	 */
	Collection<String> doRoute(ExecuteContext ctx);
	
}
