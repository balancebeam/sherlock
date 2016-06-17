package io.pddl.datasource;

import javax.sql.DataSource;

/**
 * 定义只读数据库的负载均衡策略
 * @author yangzz
 *
 */
public interface DataSourceReadStrategy {
	
	/**
	 * 获取策略的名称
	 * only-write 始终使用主数据库
	 * cycle 轮询
	 * cycle-w 主数据库参与轮询
	 * weight 权重
	 * weight-w 主数据库参与权重
	 * 
	 * @return String
	 */
	String getStrategyName();
	
	/**
	 * 返回用于读操作的数据源
	 * @param pds 数据库分片对象
	 * @return DataSource
	 */
	DataSource getSlaveDataSource(PartitionDataSource pds);
	
}
