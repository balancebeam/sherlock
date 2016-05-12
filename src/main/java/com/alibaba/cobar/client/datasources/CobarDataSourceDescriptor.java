/**
 * Copyright 1999-2011 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.alibaba.cobar.client.datasources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.cobar.client.exception.ShardingDataSourceException;
import com.alibaba.cobar.client.wrapper.CobarDataSourceWrapper;

/**
 * {@link CobarDataSourceDescriptor} describe a data base deployment structure
 * with 2 databases as HA group.<br>
 * it looks like:<br>
 * 
 * <pre>
 *                  Client
 *                    /\
 *                  /    \
 *         Active DB <-> Standby DB
 * </pre> {@link #targetDataSource} should be the reference to the current active
 * database, while {@link #standbyDataSource} should be the standby database.<br>
 * for both {@link #targetDataSource} and {@link #standbyDataSource}, each one
 * should have a sibling data source that connect to the same target database.<br>
 * as to the reason why do so, that's :
 * <ol>
 * <li>these sibling DataSources will be used when do
 * database-status-detecting.(if we fetch connection from target data source,
 * when it's full, the deteting behavior can't be performed.)</li>
 * <li>if the {@link #targetDataSource} and {@link #standbyDataSource} are
 * DataSource implementations configured in local application container, we can
 * fetch necessary information via reflection to create connection to target
 * database independently, but if they are fetched from JNDI, we can't, so
 * explicitly declaring sibling data sources is necessary in this situation.</li>
 * </ol>
 * 
 * @author fujohnwang
 * @since 1.0
 */
public class CobarDataSourceDescriptor {
    /**
     * the identity of to-be-exposed DataSource.
     */
    private String     identity;
    /**
     * active data source
     */
    private DataSource writeDataSource;
    
    /**
     * add by yangzz
     */
    private List<DataSource> readDataSources;
    /**
     * detecting data source for active data source
     */
    private DataSource targetDetectorDataSource;
    /**
     * standby data source
     */
    private DataSource standbyDataSource;
    /**
     * detecting datasource for standby data source
     */
    private DataSource standbyDetectorDataSource;

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
     * 0 only use write dataSource to execute DML SQL and DQL SQL
     * 1 default, use write dataSource to execute DML SQL, use read dataSource to execute DQL SQL
     * 2 use write dataSource to execute DML SQL, user read and write dataSource to execute DQL SQL
     */
    private int readWriteStrategy= 1;
    
    private boolean defaultDataSource= false;
    
	public void setReadWriteStrategy(int readWriteStrategy){
		if(readWriteStrategy< 0 ||readWriteStrategy> 2){
			readWriteStrategy= 0;
		}
		this.readWriteStrategy= readWriteStrategy;
	}
	
	public void setDefaultDataSource(boolean defaultDataSource){
		this.defaultDataSource= defaultDataSource;
	}
	
	public boolean isDefaultDataSource(){
		return defaultDataSource;
	}

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public DataSource getWriteDataSource() {
        return writeDataSource;
    }

    public void setWriteDataSource(DataSource writeDataSource) {
    	Assert.notNull(writeDataSource);
    	if(this.writeDataSource!= null){
    		throw new ShardingDataSourceException("Partition ["+identity+"] write dataSource setting is duplicated");
    	}
        this.writeDataSource = new CobarDataSourceWrapper(this,writeDataSource);
    }
    
    public DataSource getReadDataSource(){
    	if(CollectionUtils.isEmpty(readDataSources)){
    		return writeDataSource;
    	}
    	else if(readWriteStrategy== 1){
    		return getDataSourceByWeight(0);
    	}
    	else if(readWriteStrategy== 2){
    		return getDataSourceByWeight(((CobarDataSourceWrapper)writeDataSource).getWeight());
    	}
    	return writeDataSource;
    }
    
    private DataSource getDataSourceByWeight(int writeWeight){
    	int total= 0;
		for(DataSource ds: readDataSources){
			total+= ((CobarDataSourceWrapper)ds).getWeight();
		}
		Random rand = new Random();
		int weight= 0,rdm= rand.nextInt(total+ writeWeight);
		if(rdm< total){
			for(DataSource ds: readDataSources){
    			weight+= ((CobarDataSourceWrapper)ds).getWeight();
    			if(weight> rdm){
    				return ds;
    			}
    		}
		}
		return writeDataSource;
    }
    
    public void setReadDataSources(List<DataSource> readDataSources){
    	Assert.notEmpty(readDataSources);
    	if(!CollectionUtils.isEmpty(this.readDataSources)){
    		throw new ShardingDataSourceException("Partition ["+identity+"] read dataSources setting is duplicated");
    	}
    	this.readDataSources= new ArrayList<>();
    	for(DataSource dataSource: readDataSources){
    		this.readDataSources.add(new CobarDataSourceWrapper(this,dataSource));
    	}
    }

    public DataSource getTargetDetectorDataSource() {
        return targetDetectorDataSource;
    }

    public void setTargetDetectorDataSource(DataSource targetDetectorDataSource) {
        this.targetDetectorDataSource = targetDetectorDataSource;
    }
    
    public DataSource getStandbyDataSource() {
        return standbyDataSource;
    }

    public void setStandbyDataSource(DataSource standbyDataSource) {
        this.standbyDataSource = standbyDataSource;
    }

    public DataSource getStandbyDetectorDataSource() {
        return standbyDetectorDataSource;
    }

    public void setStandbyDetectorDataSource(DataSource standbyDetectorDataSource) {
        this.standbyDetectorDataSource = standbyDetectorDataSource;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identity == null) ? 0 : identity.hashCode());
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
        CobarDataSourceDescriptor other = (CobarDataSourceDescriptor) obj;
        if (identity == null) {
            if (other.identity != null)
                return false;
        } else if (!identity.equals(other.identity))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CobarDataSourceDescriptor [identity=" + identity + ", poolSize=" + poolSize
                + ", standbyDataSource=" + standbyDataSource + ", standbyDetectorDataSource="
                + standbyDetectorDataSource + ", writeDataSource=" + writeDataSource
                + ", readDataSources=" + readDataSources + "]";
    }
    
}
