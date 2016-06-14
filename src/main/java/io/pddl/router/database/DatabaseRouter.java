package io.pddl.router.database;

import java.util.Collection;

import io.pddl.executor.ExecuteContext;

/**
 * 数据库路由解析引擎
 * @author yangzz
 *
 */
public interface DatabaseRouter {
	
	/**
	 * 路由数据库操作
	 * @param ctx ExecuteContext SQL语句执行上下文
	 * @return Collection<String>
	 */
	Collection<String> doRoute(ExecuteContext ctx);
	
}
