package io.anyway.sherlock.router.table.config;

import java.util.Collections;
import java.util.List;

import io.anyway.sherlock.exception.ShardingTableException;
import io.anyway.sherlock.router.strategy.config.ShardingStrategyConfig;
import org.springframework.beans.factory.InitializingBean;

public class LogicTableConfig extends AbstractLogicTableConfig implements InitializingBean{

	private List<String> tablePostfixies= Collections.emptyList();

	private List<String> partitionDataSourceNames= Collections.emptyList();
	
	private ShardingStrategyConfig tableStrategyConfig;
	
	private ShardingStrategyConfig dataSourceStrategyConfig;
	
	public void setTablePostfixes(List<String> tablePostfixies){
		this.tablePostfixies= tablePostfixies;
	}
	
	@Override
	public List<String> getTablePostfixes(){
		return tablePostfixies;
	}
	
	public void setTableStrategyConfig(ShardingStrategyConfig tableStrategyConfig){
		this.tableStrategyConfig= tableStrategyConfig;
	}
	
	@Override
	public ShardingStrategyConfig getTableStrategyConfig(){
		return tableStrategyConfig;
	}
	
	public void setDatabaseStrategyConfig(ShardingStrategyConfig databaseStrategyConfig){
		this.dataSourceStrategyConfig= databaseStrategyConfig;
	}

	@Override
	public List<String> getPartitionDataSourceNames(){
		return partitionDataSourceNames;
	}

	public void setPartitionDataSourceNames(List<String> partitionDataSourceNames){
		this.partitionDataSourceNames= partitionDataSourceNames;
	}
	
	@Override
	public ShardingStrategyConfig getDataSourceStrategyConfig(){
		return dataSourceStrategyConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//check database and table strategy
		if(getDataSourceStrategyConfig()== null && getTableStrategyConfig()== null){
			throw new ShardingTableException("Logic table "+getName()+" must config database or table strategy.");
		}
	}
}
