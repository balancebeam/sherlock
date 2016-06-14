package io.pddl.router.strategy.config;

import java.util.List;

import io.pddl.router.strategy.ShardingStrategy;

public class ShardingStrategyConfig{
	
	private ShardingStrategy strategy;
	
	private List<String> columns;
	
	public void setColumns(List<String> columns){
		this.columns= columns;
	}
	
	/**
	 * 路由列名集合
	 * @return
	 */
	public List<String> getColumns(){
		return columns;
	}
	
	public void setStrategy(ShardingStrategy strategy){
		this.strategy= strategy;
	}
	
	public ShardingStrategy getStrategy(){
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
