package io.anyway.sherlock.router;

import java.util.List;

import io.anyway.sherlock.exception.SQLParserException;
import io.anyway.sherlock.executor.ExecuteContext;
import io.anyway.sherlock.router.support.SQLExecutionUnit;

/**
 * 路由SQL语句，包括数据源路由和表路由
 * @author yangzz
 *
 */
public interface SQLRouter {

	/**
	 * 路由SQL语句
	 * @param ctx ExecuteContext执行上下文
	 * @param logicSql 逻辑SQL
	 * @param parameters prepared对应的字段
	 * @return List<SQLExecutionUnit>
	 * @throws SQLParserException
	 */
	List<SQLExecutionUnit> doRoute(ExecuteContext ctx, String logicSql, List<Object> parameters) throws SQLParserException;

}
