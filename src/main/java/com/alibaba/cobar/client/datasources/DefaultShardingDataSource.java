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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.alibaba.cobar.client.datasources.ha.IHADataSourceCreator;
import com.alibaba.cobar.client.datasources.ha.NonHADataSourceCreator;
import com.alibaba.cobar.client.exception.ShardingDataSourceException;

/**
 * StrongRefDataSourceLocator is mainly responsible for assembling data sources
 * mapping relationship as per data source definitions in spring container.
 * 
 * @author fujohnwang
 */
public class DefaultShardingDataSource implements IShardingDataSource, InitializingBean {
    private Set<DataSourceDescriptor> dataSourceDescriptors   = new HashSet<>();
    private LinkedHashMap<String,DataSourceDescriptor> dataSourceMapping= new LinkedHashMap<>();
    //private List<IDataSourcePostProcessor> dataSourcePostProcessor = new ArrayList<IDataSourcePostProcessor>();
    private IHADataSourceCreator           haDataSourceCreator;
    private DataSourceDescriptor defaultDataSourceDescriptor;
    
	public Set<String> getDataSourceNames(){
		return dataSourceMapping.keySet();
	}

    public void afterPropertiesSet() throws Exception {
        if (getHaDataSourceCreator() == null) {
            setHaDataSourceCreator(new NonHADataSourceCreator());
        }
        if (dataSourceDescriptors.isEmpty()) {
        	throw new ShardingDataSourceException("do not configure any sharding datasource.");
        }
        
        for(Iterator<DataSourceDescriptor> item= dataSourceDescriptors.iterator();item.hasNext();){
        	DataSourceDescriptor dataSourceDescriptor= item.next();
        	String partition= dataSourceDescriptor.getIdentity();
        	dataSourceMapping.put(partition,dataSourceDescriptor);
        	if(dataSourceDescriptor.isDefaultDataSource()){
        		if(defaultDataSourceDescriptor!= null){
        			throw new ShardingDataSourceException("default datasource setting was duplicated");
        		}
        		defaultDataSourceDescriptor= dataSourceDescriptor;
        	}
        }
        if(defaultDataSourceDescriptor==null){
        	defaultDataSourceDescriptor= dataSourceDescriptors.iterator().next();
        }
        
        //TODO HA standby datasource

//        for (CobarDataSourceDescriptor descriptor : getDataSourceDescriptors()) {
//            Validate.notEmpty(descriptor.getIdentity());
//            Validate.notNull(descriptor.getTargetDataSource());
//
//            DataSource dataSourceToUse = descriptor.getTargetDataSource();
//
//            if (descriptor.getStandbyDataSource() != null) {
//                dataSourceToUse = getHaDataSourceCreator().createHADataSource(descriptor);
//                if (CollectionUtils.isNotEmpty(dataSourcePostProcessor)) {
//                    for (IDataSourcePostProcessor pp : dataSourcePostProcessor) {
//                        dataSourceToUse = pp.postProcess(dataSourceToUse);
//                    }
//                }
//            }
//
//            dataSources.put(descriptor.getIdentity(), new LazyConnectionDataSourceProxy(
//                    dataSourceToUse));
//        }
    }

    public void setDataSourceDescriptors(Set<DataSourceDescriptor> dataSourceDescriptors) {
        this.dataSourceDescriptors = dataSourceDescriptors;
    }

    public DataSourceDescriptor getDataSourceDescriptor(String partition) {
        return dataSourceMapping.get(partition);
    }

    public void setHaDataSourceCreator(IHADataSourceCreator haDataSourceCreator) {
        this.haDataSourceCreator = haDataSourceCreator;
    }

    public IHADataSourceCreator getHaDataSourceCreator() {
        return haDataSourceCreator;
    }

	@Override
	public DataSourceDescriptor getDefaultDataSourceDescriptor() {
		return defaultDataSourceDescriptor;
	}

}
