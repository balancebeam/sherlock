package io.pddl.router.datasource.support;

import java.util.Collection;
import java.util.Collections;

import io.pddl.executor.ExecuteContext;
import io.pddl.router.datasource.DataSourceRouter;

public class TenantDataSourceRouterSupport implements DataSourceRouter{

	@Override
	public Collection<String> doRoute(ExecuteContext ctx) {
		return Collections.singletonList("p0");
	}

}
