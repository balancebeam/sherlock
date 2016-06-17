package io.pddl.router.database.support;

import java.util.Collection;
import java.util.Collections;

import io.pddl.executor.ExecuteContext;
import io.pddl.router.database.DatabaseRouter;

public class TenantDatabaseRouterSupport implements DatabaseRouter{

	@Override
	public Collection<String> doRoute(ExecuteContext ctx) {
		return Collections.singletonList("p0");
	}

}
