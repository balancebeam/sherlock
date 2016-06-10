package io.pddl.router.strategy.value;

import java.util.List;

public class ShardingCollectionValue<T extends Comparable<?>> extends ShardingValue<T>{
	
	public ShardingCollectionValue(String column,List<T> value){
		super(column,value);
	}
	
	public List<T> getCollectionValue(){
		return getValue();
	}
	
}
