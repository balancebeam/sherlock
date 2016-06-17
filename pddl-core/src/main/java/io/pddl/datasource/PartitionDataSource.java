package io.pddl.datasource;

import javax.sql.DataSource;
/**
 * 
 * 分片数据源定义
 * @author yangzz
 *
 */
public interface PartitionDataSource{
   
	/**
	 * 获取分片数据源的名称
	 * @return String
	 */
	String getName();
	
	/**
	 * 获取主数据源
	 * @return DataSource
	 */
	DataSource getMasterDataSource();
	
	/**
	 * 根据多种策略获取只读数据源
	 * @return DataSource
	 */
	DataSource getSlaveDataSource();
	
	/**
	 * 获取数据源执行线程池大小
	 * @return
	 */
	int getPoolSize();
	
	/**
	 * 设置执行超时时间，单位为秒
	 * @return
	 */
	int getTimeout();
}
