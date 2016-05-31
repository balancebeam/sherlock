package io.pddl.table;

import java.util.Collection;

public interface LogicTableRouter {

	Collection<String> doRoute(String sql, Object[] parameters);
}
