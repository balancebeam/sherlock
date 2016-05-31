package io.pddl.table.model;

import java.util.List;

import io.pddl.table.LogicPrimaryTable;

public class LogicPrimaryTableConfig extends AbstractLogicTableConfig implements LogicPrimaryTable{

	private List<String> partitions;
	
	private List<String> postfixies;
	
	private LogicTableStrategyConfig strategyConfig;
	
	public void setPartitions(List<String> partitions){
		this.partitions= partitions;
	}
	
	@Override
	public List<String> getPartitions(){
		return partitions;
	}
	
	public void setPostfixes(List<String> postfixies){
		this.postfixies= postfixies;
	}
	
	@Override
	public List<String> getPostfixes(){
		return postfixies;
	}
	
	public void setStrategy(LogicTableStrategyConfig strategyConfig){
		this.strategyConfig= strategyConfig;
	}
	
	@Override
	public LogicTableStrategyConfig getStrategyConfig(){
		return strategyConfig;
	}
}
