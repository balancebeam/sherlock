package io.anyway.sherlock.router.strategy;

import java.util.Collection;
import java.util.List;

import io.anyway.sherlock.executor.ExecuteContext;
import io.anyway.sherlock.router.strategy.value.ShardingValue;

public interface ShardingStrategy {
	
	/**
	 * 根据传入的值进行分片操作（数据源或表）
	 * 
	 * 对于用户定义的sharding-columns列之间是and关系，且各列和表的映射关系都能取到值sharding-columns="a,b" ,values=[[1,and 2],or [3,and 4]]解析值的关系第一层是or第二层是and
	 * sql= "select name form emp where a =2"; sharding-columns="id";则取到的values值是 [[]];
	 * sql= "select name form emp where id =2"; sharding-columns="id";则取到的values值是 [[Single(2)]];
	 * sql= "select name form emp where id =2 or id=3"; sharding-columns="id";则取到的values值是 [[Single(2)],[Single(3)]];
	 * sql= "select name form emp where id in (2,3,4)"; sharding-columns="id";则取到的values值是 [[In(2,3,4)]];
	 * sql= "select name form emp where id in between 2 and 6 "; sharding-columns="id";则取到的values值是 [[Between(2,6)]];
	 * sql= "select name form emp where (id =2 or a=1) and (id=2 or b=2)"; sharding-columns="id";则取到的values值是 [[Single(2)],[Single(2)]];
	 * sql= "select name form emp where (id =2 and a=1) or (id=3 and b=2)"; sharding-columns="id";则取到的values值是 [[Single(2)],[Single(3)]];
	 * sql= "select name form emp where id=2 or id in (4,6)"; sharding-columns="id";则取到的values值是 [[Single(2)],[In(2,4,6)]];
	 * sql= "select name form emp where id=2 or id between 4 and 6"; sharding-columns="id";则取到的values值是 [[Single(2)],[Between(4,6)]];
	 * 
	 * sql= "select name from emp where id=2; sharding-columns="id,a";则取到的values值是[[]]
	 * sql= "select name from emp where id=2 and a=3; sharding-columns="id,a";则取到的values值是 [[Single(2),Single(3)]];
	 * sql= "select name from emp where id=2 or a=3; sharding-columns="id,a";则取到的values值是 [[]]; id和a不是and关系,暂时处理不了这个问题一般是配置的不对
	 * sql= "select name from emp where (id=2 and a=3) or (id=4 and a=5); sharding-columns="id,a";则取到的values值是 [[Single(2),Single(3)],[Single(4),Single(5)]];
	 * sql= "select name from emp where (id=2 or a=3) and (id=4 or a=5); sharding-columns="id,a";则取到的values值是 [[Single(2),Single(5)],[Single(4),Single(3)]]
	 * 
	 * @param ctx 执行上下文
	 * @param availableNames 可用的列表
	 * @param shardingValues 值列表
	 * @return Collection<String>
	 */
	Collection<String> doSharding(ExecuteContext ctx,Collection<String> availableNames,List<ShardingValue<?>> shardingValues);
}
