package io.pddl.audit;

import java.util.List;

public interface SqlAuditor {
	void audit(String sql, List<Object> parameters);
}
