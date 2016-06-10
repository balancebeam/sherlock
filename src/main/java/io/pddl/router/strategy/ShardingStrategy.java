package io.pddl.router.strategy;

import java.util.Collection;
import java.util.List;

import io.pddl.router.strategy.value.ShardingValue;

public interface ShardingStrategy {
	
	Collection<String> doSharding(Collection<String> availableNames,List<ShardingValue<?>> values);
}
