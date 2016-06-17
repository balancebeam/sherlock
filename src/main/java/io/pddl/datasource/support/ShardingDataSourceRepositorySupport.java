package io.pddl.datasource.support;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DatabaseType;
import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.exception.ShardingDataSourceException;

/**
 * 多数据源仓库
 * @author yangzz
 *
 */
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
	public PartitionDataSource getDefaultDataSource() {
		return defaultPartitionDataSource;
	}
	
    @Override
    public void afterPropertiesSet() throws Exception {
        if (partitionDataSources.isEmpty()) {
        	throw new ShardingDataSourceException("do not configure any sharding datasource.");
        }
        Log logger = LogFactory.getLog(ShardingDataSourceRepositorySupport.class);
        for(Iterator<PartitionDataSource> item= partitionDataSources.iterator();item.hasNext();){
        	PartitionDataSource partitionDataSource= item.next();
        	String partition= partitionDataSource.getName();
        	if(logger.isInfoEnabled()){
        		logger.info("init partition datasource: "+partitionDataSource);
        	}
        	partitionDataSourceMapping.put(partition,partitionDataSource);
        	if(((PartitionDataSourceSupport)partitionDataSource).isDefaultDataSource()){
        		if(defaultPartitionDataSource!= null){
        			throw new ShardingDataSourceException("default datasource setting was duplicated");
        		}
        		defaultPartitionDataSource= partitionDataSource;
        	}
        }
        if(defaultPartitionDataSource==null){
        	defaultPartitionDataSource= partitionDataSources.iterator().next();
        	if(logger.isInfoEnabled()){
        		logger.info("default datasource: "+defaultPartitionDataSource);
        	}
        }
    }
}
