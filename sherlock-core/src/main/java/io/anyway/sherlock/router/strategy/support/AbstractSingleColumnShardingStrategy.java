package io.anyway.sherlock.router.strategy.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.anyway.sherlock.router.strategy.value.ShardingCollectionValue;
import io.anyway.sherlock.router.strategy.value.ShardingRangeValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.anyway.sherlock.exception.ShardingTableException;
import io.anyway.sherlock.executor.ExecuteContext;
import io.anyway.sherlock.router.strategy.ShardingStrategy;
import io.anyway.sherlock.router.strategy.value.ShardingSingleValue;
import io.anyway.sherlock.router.strategy.value.ShardingValue;

public abstract class AbstractSingleColumnShardingStrategy<T extends Comparable<?>> implements ShardingStrategy{
	
	protected Log logger = LogFactory.getLog(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> doSharding(ExecuteContext ctx,Collection<String> availableNames, List<ShardingValue<?>> shardingValues) {
		//如果存在多column则不支持
		if(shardingValues.size()> 1){
			throw new ShardingTableException("not support multiple column condition strategy");
		}
		Set<String> result= new HashSet<String>();
		ShardingValue<?> shardingValue=  shardingValues.get(0);
		String column= shardingValue.getColumn();
		if(shardingValue instanceof ShardingSingleValue){
			ShardingSingleValue<T> singleValue= (ShardingSingleValue<T>)shardingValue;
			T value= singleValue.getSingleValue();
			result.add(doEqualSharding(ctx,availableNames,column,value));
		}
		else if(shardingValue instanceof ShardingCollectionValue){
			ShardingCollectionValue<?> collectionValue= (ShardingCollectionValue<?>)shardingValue;
			List<T> value= (List<T>)collectionValue.getCollectionValue();
			result.addAll(doInSharding(ctx,availableNames,column,value));
		}
		else if(shardingValue instanceof ShardingRangeValue){
			ShardingRangeValue<T> rangeValue= (ShardingRangeValue<T>)shardingValue;
			T lower= rangeValue.getLower();
			T upper= rangeValue.getUpper();
			result.addAll(doBetweenSharding(ctx,availableNames,column,lower,upper));
		}
		return result;
	}
	
	public abstract String doEqualSharding(ExecuteContext ctx,Collection<String> availableNames, String column,T value);
	
	public abstract Collection<String> doInSharding(ExecuteContext ctx,Collection<String> availableNames, String column,List<T> values);
	
	public abstract Collection<String> doBetweenSharding(ExecuteContext ctx,Collection<String> availableNames, String column,T lower,T upper);
}
