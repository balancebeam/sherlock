package io.pddl.router.strategy.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import io.pddl.exception.ShardingTableException;
import io.pddl.router.strategy.ShardingStrategy;
import io.pddl.router.strategy.value.ShardingCollectionValue;
import io.pddl.router.strategy.value.ShardingRangeValue;
import io.pddl.router.strategy.value.ShardingSingleValue;
import io.pddl.router.strategy.value.ShardingValue;

public abstract class AbstractSingleShardingStrategy<T extends Comparable<?>> implements ShardingStrategy{

	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> doSharding(Collection<String> availableNames, List<ShardingValue<?>> shardingValues) {
		if(CollectionUtils.isEmpty(shardingValues)){
			return availableNames;
		}
		if(shardingValues.size()> 1){
			throw new ShardingTableException("not support multiple condition strategy");
		}
		ShardingValue<T> shardingValue= (ShardingValue<T>)shardingValues.get(0);
		Set<String> result= new HashSet<String>();
		String column= shardingValue.getColumn();
		if(shardingValue instanceof ShardingSingleValue){
			ShardingSingleValue<T> singleValue= (ShardingSingleValue<T>)shardingValue;
			T value= singleValue.getSingleValue();
			result.add(doEqualSharding(availableNames,column,value));
		}
		else if(shardingValue instanceof ShardingCollectionValue){
			ShardingCollectionValue<?> collectionValue= (ShardingCollectionValue<?>)shardingValue;
			List<T> value= (List<T>)collectionValue.getCollectionValue();
			result.addAll(doInSharding(availableNames,column,value));
		}
		else if(shardingValue instanceof ShardingRangeValue){
			ShardingRangeValue<T> rangeValue= (ShardingRangeValue<T>)shardingValue;
			T lower= rangeValue.getLower();
			T upper= rangeValue.getUpper();
			result.addAll(doBetweenSharding(availableNames,column,lower,upper));
		}
		return result;
	}
	
	public abstract String doEqualSharding(Collection<String> availableNames, String column,T value);
	
	public abstract Collection<String> doInSharding(Collection<String> availableNames, String column,List<T> values);
	
	public abstract Collection<String> doBetweenSharding(Collection<String> availableNames, String column,T lower,T upper);
}
