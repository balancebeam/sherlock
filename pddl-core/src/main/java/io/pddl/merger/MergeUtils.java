package io.pddl.merger;

import io.pddl.executor.ExecuteContext;
import io.pddl.merger.pipeline.reducer.IteratorReducerResultSet;
import io.pddl.merger.pipeline.reducer.StreamingOrderByReducerResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MergeUtils {

	public static ResultSet mergeResultSet(List<ResultSet> resultSets, ExecuteContext ctx) throws SQLException {
		MergeContext mc = new MergeContext(resultSets, ctx);
		/*if (resultSets.size() == 0) {
			return new IteratorReducerResultSet(mc);
		}
		if (resultSets.size() == 1) {
			return resultSets.get(0);
		}
		if (mc.hasGroupColumn() && mc.hasOrderColumn()) {
			// TODO
		}

		if (mc.hasGroupColumn()) {
			// TODO
		}
		if (mc.hasOrderColumn()) {
			return new StreamingOrderByReducerResultSet(mc);
		}
		return new IteratorReducerResultSet(mc);*/


		return ResultSetFactory.getResultSet(mc);
	}
	
	public static int mergeIntegerResult(List<Integer> result){
		int total= 0;
    	for(int it: result){
    		total+= it;
    	}
    	return total;
	}
}
