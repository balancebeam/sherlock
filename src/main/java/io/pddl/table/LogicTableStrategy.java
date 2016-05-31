package io.pddl.table;

import java.util.Collection;
import java.util.List;

public interface LogicTableStrategy {
	
	Collection<String> doSharding(LogicTable table,List<ShardingValue<?>> values);
}
