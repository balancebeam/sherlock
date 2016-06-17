package io.pddl.executor;

import java.util.List;

import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.jdbc.ShardingConnection;
import io.pddl.router.table.GlobalTableRepository;
import io.pddl.router.table.LogicTableRepository;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.bean.SQLStatementType;

/**
 * SQL Sharding的上下文，在创建ShardingConnection时创建
 * @author yangzz
 *
 */
public interface ExecuteContext{
	
	/**
	 * 获取对应的ShardingConnection对象，一一对应的关系
	 * @return ShardingConnection
	 */
	ShardingConnection getShardingConnection();
	
	/**
	 * 是否是不带事务的查询语句
	 * @return true | false
	 */
	boolean isSimplyDQLOperation();
	
	/**
	 * 是否是增删改操作
	 * @return true | false
	 */
	boolean isDMLOperation();
	
	/**
	 * 获取SQL的类型
	 * @return SQLStatementType.[SELECT | INSERT | DELETE | UPDATE]
	 */
	SQLStatementType getStatementType();
	
	/**
	 * 获取SQL解析结果，主要包含表名和条件内容等
	 * @return SQLParsedResult
	 */
	SQLParsedResult getSQLParsedResult();
	
	/**
	 * 获取原始逻辑SQL语句
	 * @return String
	 */
	String getLogicSql();
	
	/**
	 * 获取Prepared SQL对应的传值
	 * @return List<Object>
	 */
	List<Object> getParameters();
	
	/**
	 * 获取多数据源仓库
	 * @return ShardingDataSourceRepository
	 */
	ShardingDataSourceRepository getShardingDataSourceRepository();
	
	/**
	 * 获取全局/字典表仓库
	 * @return GlobalTableRepository
	 */
	GlobalTableRepository getGlobalTableRepository();
	
	/**
	 * 获取逻辑表仓库
	 * @return LogicTableRepository
	 */
	LogicTableRepository getLogicTableRepository();
	
}
