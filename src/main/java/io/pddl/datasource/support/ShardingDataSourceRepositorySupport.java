package io.pddl.datasource.support;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DatabaseType;
import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.exception.ShardingDatabaseException;

public class ShardingDataSourceRepositorySupport implements ShardingDataSourceRepository,InitializingBean {
	
    private Set<PartitionDataSource> partitionDataSources;
    
    private LinkedHashMap<String,PartitionDataSource> partitionDataSourceMapping= new LinkedHashMap<String,PartitionDataSource>();
    
    private PartitionDataSource defaultPartitionDataSource;
    
    public void setDatabaseType(DatabaseType databaseType){
    	DatabaseType.setApplicationDatabaseType(databaseType);
    }
    
    @Override
	public Set<String> getPartitionDataSourceNames(){
		return partitionDataSourceMapping.keySet();
	}
    
    public void setPartitionDataSources(Set<PartitionDataSource> partitionDataSources) {
        this.partitionDataSources = partitionDataSources;
    }

    @Override
    public PartitionDataSource getPartitionDataSource(String partition) {
        return partitionDataSourceMapping.get(partition);
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
        	
        	partitionDataSourceMapping.put(partition,partitionDataSource);
        	if(((PartitionDataSourceSupport)partitionDataSource).isDefaultDataSource()){
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
