package io.anyway.sherlock.router.database.support;

import java.util.Collection;
import java.util.Collections;

import io.anyway.sherlock.router.database.DatabaseRouter;
import io.anyway.sherlock.executor.ExecuteContext;

public class TenantDatabaseRouterSupport implements DatabaseRouter {

	@Override
	public Collection<String> doRoute(ExecuteContext ctx) {
		return Collections.singletonList("p0");
	}

}
