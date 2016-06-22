package io.pddl.merger.pipeline.reducer;

import io.pddl.merger.MergeContext;
import io.pddl.merger.resultset.memory.AbstractMemoryOrderByResultSet;

import java.sql.SQLException;

/**
 * 根据排序列内存排序的聚集结果集.
 *
 * @author xiong.j
 */
public final class GroupByReducerResultSet extends AbstractMemoryOrderByResultSet {

    public GroupByReducerResultSet(MergeContext mc) throws SQLException {
        super(mc.getResultSets(), mc.getOrderColumns());
    }
}
