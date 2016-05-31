package io.pddl.table;

public interface LogicTableRouter {

	String[] doRoute(String sql, Object[] parameters);
}
