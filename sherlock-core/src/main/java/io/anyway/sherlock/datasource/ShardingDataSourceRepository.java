package io.anyway.sherlock.datasource;

import java.util.Set;

/**
 * 定义分片数据源仓库
 * @author yangzz
 *
 */
public interface ShardingDataSourceRepository {
	
	/**
	 * 获取所有可用的分片数据源名称
	 * @return Set<String>
	 */
	Set<String> getPartitionDataSourceNames();
	
	/**
	 * 获取指定名称的分片数据源
	 * @param name 分片名称
	 * @return PartitionDataSource
	 */
	PartitionDataSource getPartitionDataSource(String name);
	
	/**
	 * 获取默认的数据源
	 * @return
	 */
	PartitionDataSource getDefaultDataSource();
	/**
	 * 获取数据库类型
	 * @return
	 */
	DatabaseType getDatabaseType();
}
