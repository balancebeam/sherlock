package io.pddl.router.table.value;

import java.util.Collections;
import java.util.List;

public class ShardingSingleValue<T extends Comparable<?>> extends ShardingValue<T>{

	public ShardingSingleValue(String column,T value){
		super(column);
		this.setValue(Collections.singletonList(value));
	}
	
	public ShardingSingleValue(String column,List<T> value){
		super(column,value);
	}
	
	public T getSingleValue(){
		return getValue().get(0);
	}
	
}
