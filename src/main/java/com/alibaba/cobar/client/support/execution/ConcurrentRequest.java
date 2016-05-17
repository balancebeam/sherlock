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
 package com.alibaba.cobar.client.support.execution;

import java.util.concurrent.ExecutorService;

import javax.sql.DataSource;

import org.springframework.orm.ibatis.SqlMapClientCallback;

import com.alibaba.cobar.client.datasources.PartitionDataSource;
/**
 * {@link #action} will be executed on {@link #dataSource} with {@link #executor} asynchronously.<br>
 * 
 * @author fujohnwang
 * @since  1.0 
 */
public class ConcurrentRequest {
    private SqlMapClientCallback action;
    private PartitionDataSource           partitionDataSource;
    private ExecutorService      executor;
//    private boolean writable;
    private DataSource dataSource;

    public SqlMapClientCallback getAction() {
        return action;
    }

    public void setAction(SqlMapClientCallback action) {
        this.action = action;
    }

    public PartitionDataSource getPartitionDataSource() {
        return partitionDataSource;
    }

    public void setPartitionDataSource(PartitionDataSource partitionDataSource) {
        this.partitionDataSource = partitionDataSource;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
    
//    public void setWritable(boolean writable){
//    	this.writable= writable;
//    }
//    
//    public boolean isWritable(){
//    	return writable;
//    }
    
    public void setDataSource(DataSource dataSource){
    	this.dataSource= dataSource;
    }
    
    public DataSource getDataSource(){
    	return dataSource;
    }

}
