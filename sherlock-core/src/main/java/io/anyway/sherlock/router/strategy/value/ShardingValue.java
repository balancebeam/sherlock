package io.anyway.sherlock.router.strategy.value;

import java.util.List;

public abstract class ShardingValue<T extends Comparable<?>>{

	private String column;
	
	private List<T> value;
	
	public ShardingValue(String column){
		this.column= column;
	}
	
	public ShardingValue(String column,List<T> value){
		this.column= column;
		this.value= value;
	}
	
	public String getColumn() {
		return column;
	}
	
	protected void setValue(List<T> value){
		this.value= value;
	}
	
	protected List<T> getValue(){
		return value;
	}
}
