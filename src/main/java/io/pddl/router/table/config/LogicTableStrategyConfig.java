package io.pddl.router.table.config;

import java.util.List;

import io.pddl.router.table.strategy.LogicTableStrategy;

public class LogicTableStrategyConfig {
	
	private List<String> columns;
	
	private LogicTableStrategy strategy;
	
	public void setColumns(List<String> columns){
		this.columns= columns;
	}
	
	public List<String> getColumns(){
		return columns;
	}
	
	public void setStrategy(LogicTableStrategy strategy){
		this.strategy= strategy;
	}
	
	public LogicTableStrategy getStrategy(){
		return strategy;
	}
	
	@Override
	public String toString(){
		StringBuilder builder= new StringBuilder();
		builder.append("[ ")
		.append("columns=")
		.append(getColumns())
		.append(", ")
		.append("function=")
		.append(strategy)
		.append(" ]");
		return builder.toString();
	}
}
