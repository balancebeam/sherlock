package io.pddl.router.table;

import java.util.Collection;

import io.pddl.executor.ExecuteContext;

public interface LogicTableRouter {

	Collection<String> doRoute(ExecuteContext ctx,String dataSourceName);
}
