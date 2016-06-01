package io.pddl.datasource.support;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.util.Assert;

import io.pddl.datasource.DatabaseReadStrategy;
import io.pddl.datasource.PartitionDataSource;

public class DefaultPartitionDataSource implements PartitionDataSource{
	/**
     * the name of to-be-exposed DataSource.
     */
    private String name;
    /**
     * active data source
     */
    private DataSource writeDataSource;
    /**
     * read dataSource list
     */
    private List<DataSource> readDataSources;
    /**
     * we will initialize proper thread pools which stand in front of data
     * sources as per connection pool size. <br>
     * usually, they will have same number of objects.<br>
     * you have to set a proper size for this attribute as per your data source
     * attributes. In case you forget it, we set a default value with
     * "number of CPU" * 5.
     */
    private int poolSize = Runtime.getRuntime().availableProcessors() * 2;
    
    /**
     * only-write: use write dataSource only to execute DQL SQL ,default read strategy.
     * polling: use read dataSource only to execute DQL SQL with polling read strategy.
     * polling-w: use read and write dataSource to execute DQL SQL with polling read strategy.
     * power: use read dataSource only to execute DQL SQL with power read strategy.
     * power-w: use read and write dataSource to execute DQL SQL with power read strategy.
     * weight: use read dataSource only to execute DQL SQL with weight read strategy.
     * weight-w: use read and write dataSource to execute DQL SQL with weight read strategy.
     */
    private String readStrategy= "only-write";
    
    private boolean defaultDataSource= false;
    
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
    public DataSource getWriteDataSource() {
        return writeDataSource;
    }
    
    public void setWriteDataSource(DataSource writeDataSource) {
    	Assert.notNull(writeDataSource);
    	if(!(writeDataSource instanceof DefaultDataSourceProxy)){
    		writeDataSource= new DefaultDataSourceProxy(writeDataSource);
    	}
    	((DefaultDataSourceProxy)writeDataSource).setPartitionDataSource(this);
    	this.writeDataSource= writeDataSource;
    }
    
    @Override
    public DataSource getReadDataSource(){
    	DatabaseReadStrategy strategy= DatabaseReadStrategyRepository.getDatabaseReadStrategy(readStrategy);
    	return strategy.getReadDataSource(this);
    }
    
    public void setReadDataSources(List<DataSource> readDataSources){
    	Assert.notEmpty(readDataSources);
    	this.readDataSources= new ArrayList<DataSource>();
    	for(DataSource dataSource: readDataSources){
    		if(!(dataSource instanceof DefaultDataSourceProxy)){
    			dataSource= new DefaultDataSourceProxy(dataSource);
        	}
        	((DefaultDataSourceProxy)dataSource).setPartitionDataSource(this);
        	this.readDataSources.add(dataSource);
    	}
    }
    
    public List<DataSource> getReadDataSources(){
    	return readDataSources;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public int getPoolSize() {
        return poolSize;
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
        DefaultPartitionDataSource other = (DefaultPartitionDataSource) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartitionDataSource [name=" + name + ", poolSize=" + poolSize
                + ", writeDataSource=" + writeDataSource
                + ", readDataSources=" + readDataSources + "]";
    }
}
