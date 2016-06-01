package io.pddl.router.table.strategy;

import java.util.Collection;
import java.util.List;

import io.pddl.router.table.LogicTable;
import io.pddl.router.table.value.ShardingValue;

public interface LogicTableStrategy {
	
	Collection<String> doSharding(LogicTable table,List<ShardingValue<?>> values);
}
