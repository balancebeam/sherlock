package io.pddl.router.database;


import java.util.Collection;

import io.pddl.executor.ExecuteContext;

public interface DatabaseRouter {
	
	Collection<String> doRoute(ExecuteContext ctx);
	
}
