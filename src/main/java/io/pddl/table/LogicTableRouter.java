package io.pddl.table;

import java.sql.Connection;
import java.util.Collection;

public interface LogicTableRouter {

	Collection<String> doRoute(String sql, Object[] parameters,Connection conn);
}
