package io.pddl.router.strategy;

import java.util.Collection;
import java.util.List;

import io.pddl.executor.ExecuteContext;
import io.pddl.router.strategy.value.ShardingValue;

public interface ShardingStrategy {
	
	/**
	 * 根据传入的值进行分片操作（数据源或表）
	 * @param ctx 执行上下文
	 * @param availableNames 可用的列表
	 * @param values 值列表
	 * @return Collection<String>
	 */
	Collection<String> doSharding(ExecuteContext ctx,Collection<String> availableNames,List<ShardingValue<?>> values);
}
