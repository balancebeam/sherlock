package io.pddl.executor;

import io.pddl.router.support.SQLExecutionUnit;

public class InputWrapper<T> {
	
	private SQLExecutionUnit unit;
	
	private T input;
	
	public InputWrapper(SQLExecutionUnit unit,T input){
		this.unit= unit;
		this.input= input;
	}
	
	public SQLExecutionUnit getSQLExecutionUnit(){
		return unit;
	}
	
	public T getInput(){
		return input;
	}
}
