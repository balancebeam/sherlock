package io.pddl.merger;

import io.pddl.executor.ExecuteContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 分片结果集合归并工具类.
 *
 * @author xiong.j
 */
public class MergeUtils {

	/**
	 * 为Select集合生成处理子元素的类，真正的merge操作不在这里处理
	 *
	 * @param resultSets
	 * @param ctx
	 * @return
	 * @throws SQLException
     */
	public static ResultSet mergeResultSet(List<ResultSet> resultSets, ExecuteContext ctx) throws SQLException {
		MergeContext mc = new MergeContext(resultSets, ctx);
		return ResultSetFactory.getResultSet(mc);
	}

	/**
	 * 其它结果集合归并
	 *
	 * @param result
	 * @return
     */
	public static int mergeIntegerResult(List<Integer> result){
		int total= 0;
    	for(int it: result){
    		total+= it;
    	}
    	return total;
	}
}
