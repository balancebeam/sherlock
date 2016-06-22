package io.pddl.merger;

import io.pddl.merger.pipeline.coupling.GroupByCouplingResultSet;
import io.pddl.merger.pipeline.coupling.LimitCouplingResultSet;
import io.pddl.merger.pipeline.coupling.MemoryOrderByCouplingResultSet;
import io.pddl.merger.pipeline.reducer.GroupByReducerResultSet;
import io.pddl.merger.pipeline.reducer.IteratorReducerResultSet;
import io.pddl.merger.pipeline.reducer.StreamingOrderByReducerResultSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 分片结果集归并工厂.
 *
 * @author xiong.j
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ResultSetFactory {
    
    /**
     * 获取结果集.
     *
     * @param mergeContext 结果归并上下文
     * @return 结果集包装
     */
    public static ResultSet getResultSet(final MergeContext mergeContext) throws SQLException {
        if (mergeContext.getResultSets().isEmpty()) {
            return buildEmpty(mergeContext.getResultSets());
        } else if (mergeContext.getResultSets().size() == 1) {
            return buildSingle(mergeContext.getResultSets(), mergeContext);
        } else {
            return buildMultiple(mergeContext.getResultSets(), mergeContext);
        }
    }
    
    private static ResultSet buildEmpty(final List<ResultSet> resultSets) {
        return resultSets.get(0);
    }
    
    private static ResultSet buildSingle(final List<ResultSet> resultSets, final MergeContext mergeContext) throws SQLException {
        return mergeContext.hasLimit() ? new LimitCouplingResultSet(resultSets.get(0), mergeContext) : resultSets.get(0);
    }
    
    private static ResultSet buildMultiple(final List<ResultSet> resultSets, final MergeContext mergeContext) throws SQLException {
        return buildCoupling(buildReducer(mergeContext), mergeContext);
    }
    
    private static ResultSet buildReducer(final MergeContext mergeContext) throws SQLException {
        if (mergeContext.hasGroupColumn()) {
            return new GroupByReducerResultSet(mergeContext);
        }
        if (mergeContext.hasOrderColumn()) {
            return new StreamingOrderByReducerResultSet(mergeContext);
        }
        return new IteratorReducerResultSet(mergeContext);
    }
    
    private static ResultSet buildCoupling(final ResultSet resultSet, final MergeContext mergeContext) throws SQLException {
        ResultSet result = resultSet;
        if (mergeContext.hasGroupByOrAggregation()) {
            result = new GroupByCouplingResultSet(result, mergeContext);
        }
        if (mergeContext.isNeedMemorySortForOrderBy()) {
            result = new MemoryOrderByCouplingResultSet(result, mergeContext);
        }
        if (mergeContext.hasLimit()) {
            result = new LimitCouplingResultSet(result, mergeContext);
        }
        return result;
    }
}
