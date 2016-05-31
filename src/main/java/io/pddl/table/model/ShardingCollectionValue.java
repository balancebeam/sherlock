package io.pddl.table.model;

import java.util.List;

import io.pddl.table.ShardingValue;

public class ShardingCollectionValue<T extends Comparable<?>> extends ShardingValue<T>{
	
	public ShardingCollectionValue(String column,List<T> value){
		super(column,value);
	}
	
	public List<T> getCollectionValue(){
		return getValue();
	}
	
}
