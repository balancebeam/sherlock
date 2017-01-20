package io.anyway.sherlock.datasource.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.sql.DataSource;

import io.anyway.sherlock.datasource.DataSourceReadStrategy;
import io.anyway.sherlock.datasource.PartitionDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

public class PartitionDataSourceSupport implements PartitionDataSource,ApplicationContextAware,InitializingBean{

    private ApplicationContext applicationContext;
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
    private int poolSize = Runtime.getRuntime().availableProcessors() * 4;
    
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

    @Override
    public int getPoolSize() {
        return poolSize;
    }
    
    @Override
    public int getTimeout(){
    	return timeout;
    }

    @Override
    public ExecutorService getExecutorService() {
        return ((ThreadPoolTaskExecutor)applicationContext.getBean(name+"-"+"threadPool")).getThreadPoolExecutor();
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext= applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BeanDefinitionBuilder beanDefinitionBuilder= BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskExecutor.class);
        beanDefinitionBuilder.addPropertyValue("maxPoolSize",poolSize);
        beanDefinitionBuilder.addPropertyValue("keepAliveSeconds",timeout);

        ConfigurableApplicationContext configurableApplicationContext= (ConfigurableApplicationContext)applicationContext;
        BeanDefinitionRegistry beanDefinitionRegistry= (BeanDefinitionRegistry)configurableApplicationContext.getBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(name+"-"+"threadPool",beanDefinitionBuilder.getRawBeanDefinition());
    }
}
