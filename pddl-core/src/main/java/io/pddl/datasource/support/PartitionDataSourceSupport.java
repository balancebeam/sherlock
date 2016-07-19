package io.pddl.datasource.support;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.util.Assert;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DataSourceReadStrategy;

public class PartitionDataSourceSupport implements PartitionDataSource{
	/**
     * the name of to-be-exposed DataSource.
     */
    private String name;
    /**
     * active data source
     */
    private DataSource masterDataSource;
    /**
     * read dataSource list
     */
    private List<DataSource> slaveDataSources;
    /**
     * we will initialize proper thread pools which stand in front of data
     * sources as per connection pool size. <br>
     * usually, they will have same number of objects.<br>
     * you have to set a proper size for this attribute as per your data source
     * attributes. In case you forget it, we set a default value with
     * "number of CPU" * 5.
     */
    private int poolSize = Runtime.getRuntime().availableProcessors() * 2;
    
    private String readStrategy= "master";
    
    private boolean defaultDataSource= false;
    
    private int timeout= 60;
    
	public void setReadStrategy(String readStrategy){
		this.readStrategy= readStrategy;
	}
	
	public void setDefaultDataSource(boolean defaultDataSource){
		this.defaultDataSource= defaultDataSource;
	}
	
	public boolean isDefaultDataSource(){
		return defaultDataSource;
	}

	@Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public DataSource getMasterDataSource() {
        return masterDataSource;
    }
    
    public void setMasterDataSource(DataSource masterDataSource) {
    	Assert.notNull(masterDataSource);
    	if(!(masterDataSource instanceof WeightDataSourceProxy)){
    		masterDataSource= new WeightDataSourceProxy(masterDataSource);
    	}
    	this.masterDataSource= masterDataSource;
    }
    
    @Override
    public DataSource getSlaveDataSource(){
    	DataSourceReadStrategy strategy= DatabaseReadStrategyRepository.getDatabaseReadStrategy(readStrategy);
    	return strategy.getSlaveDataSource(this);
    }
    
    public void setSlaveDataSources(List<DataSource> slaveDataSources){
    	Assert.notEmpty(slaveDataSources);
    	this.slaveDataSources= new ArrayList<DataSource>();
    	for(DataSource dataSource: slaveDataSources){
    		if(!(dataSource instanceof WeightDataSourceProxy)){
    			dataSource= new WeightDataSourceProxy(dataSource);
        	}
        	this.slaveDataSources.add(dataSource);
    	}
    }
    
    public List<DataSource> getSlaveDataSources(){
    	return slaveDataSources;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public int getPoolSize() {
        return poolSize;
    }
    
    public void setTimeout(int timeout){
    	this.timeout= timeout;
    }
    
    @Override
    public int getTimeout(){
    	return timeout;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PartitionDataSourceSupport other = (PartitionDataSourceSupport) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DataSource [name=" + name + ", poolSize=" + poolSize
                + ", masterDataSource=" + masterDataSource
                + ", slaveDataSources=" + slaveDataSources + "]";
    }
}
