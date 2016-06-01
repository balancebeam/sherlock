package io.pddl.datasource.support;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import io.pddl.datasource.DatabaseType;
import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.ShardingDataSource;
import io.pddl.exception.ShardingDatabaseException;


public class DefaultShardingDataSource implements ShardingDataSource, InitializingBean {
	
    private Set<PartitionDataSource> partitionDataSources = new HashSet<PartitionDataSource>();
    
    private LinkedHashMap<String,PartitionDataSource> dataSourceMapping= new LinkedHashMap<String,PartitionDataSource>();
    
    private PartitionDataSource defaultPartitionDataSource;
    
    public void setDatabaseType(DatabaseType databaseType){
    	DatabaseType.setApplicationDatabaseType(databaseType);
    }
    
    @Override
	public Set<String> getPartitionDataSourceNames(){
		return dataSourceMapping.keySet();
	}
    
    public void setPartitionDataSources(Set<PartitionDataSource> partitionDataSources) {
        this.partitionDataSources = partitionDataSources;
    }

    @Override
    public PartitionDataSource getPartitionDataSource(String partition) {
        return dataSourceMapping.get(partition);
    }

	@Override
	public PartitionDataSource getDefaultPartitionDataSource() {
		return defaultPartitionDataSource;
	}
	
    @Override
    public void afterPropertiesSet() throws Exception {
        if (partitionDataSources.isEmpty()) {
        	throw new ShardingDatabaseException("do not configure any sharding datasource.");
        }
        
        for(Iterator<PartitionDataSource> item= partitionDataSources.iterator();item.hasNext();){
        	PartitionDataSource partitionDataSource= item.next();
        	String partition= partitionDataSource.getName();
        	
        	dataSourceMapping.put(partition,partitionDataSource);
        	if(((DefaultPartitionDataSource)partitionDataSource).isDefaultDataSource()){
        		if(defaultPartitionDataSource!= null){
        			throw new ShardingDatabaseException("default datasource setting was duplicated");
        		}
        		defaultPartitionDataSource= partitionDataSource;
        	}
        }
        if(defaultPartitionDataSource==null){
        	defaultPartitionDataSource= partitionDataSources.iterator().next();
        }
    }
}
