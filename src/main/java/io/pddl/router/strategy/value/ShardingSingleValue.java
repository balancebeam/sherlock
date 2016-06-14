package io.pddl.router.strategy.value;

import java.util.Collections;
import java.util.List;

/**
 * 单个值
 * @author yangzz
 *
 * @param <T>
 */
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
