package io.anyway.sherlock.router.strategy.value;

import java.util.List;

/**
 * 集合值
 * @author yangzz
 *
 * @param <T>
 */
public class ShardingCollectionValue<T extends Comparable<?>> extends ShardingValue<T>{
	
	public ShardingCollectionValue(String column,List<T> value){
		super(column,value);
	}
	
	public List<T> getCollectionValue(){
		return getValue();
	}
	
}
