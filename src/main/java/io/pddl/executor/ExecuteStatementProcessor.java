package io.pddl.executor;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import io.pddl.executor.support.ExecuteStatementWrapper;

public interface ExecuteStatementProcessor {
	
	/**
	 * 执行Statement操作集合处理器
	 * @param ctx ShardingConnection对应的上下文
	 * @param wrappers ExecuteStatementWrapper列表，IN的类型：Statement|PreparedStatement
	 * @param callback Statement处理回调方法
	 * @return 多个结果集List<String|Number|Boolean>
	 * @throws SQLException
	 */
	<IN extends Statement, OUT> List<OUT> execute(ExecuteContext ctx,List<ExecuteStatementWrapper<IN>> wrappers,ExecuteStatementCallback<IN, OUT> callback) throws SQLException;
	
}
