package io.pddl.executor.support;

import java.sql.Statement;

import io.pddl.router.support.SQLExecutionUnit;
/**
 * 
 * @author yangzz
 *
 * @param <T> Statement|PreparedStatement
 */
public class ExecuteStatementWrapper<T extends Statement> {
	
	private SQLExecutionUnit unit;
	
	private T statement;
	
	public ExecuteStatementWrapper(SQLExecutionUnit unit,T statement){
		this.unit= unit;
		this.statement= statement;
	}
	/**
	 * 获取执行单元
	 * @return SQLExecutionUnit
	 */
	public SQLExecutionUnit getSQLExecutionUnit(){
		return unit;
	}
	
	/**
	 * 获取相应的Statement对象 
	 * @return Statement|PreparedStatement
	 */
	public T getStatement(){
		return statement;
	}
}
