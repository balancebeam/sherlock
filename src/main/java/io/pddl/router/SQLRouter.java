package io.pddl.router;

import java.util.List;

import io.pddl.exception.SQLParserException;
import io.pddl.executor.ExecuteContext;
import io.pddl.router.support.SQLExecutionUnit;

public interface SQLRouter {

	List<SQLExecutionUnit> doRoute(ExecuteContext ctx, String sql, List<Object> parameters) throws SQLParserException;

}
