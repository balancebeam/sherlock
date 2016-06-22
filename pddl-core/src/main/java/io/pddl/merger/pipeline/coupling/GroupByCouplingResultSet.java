package io.pddl.merger.pipeline.coupling;

import com.google.common.base.Optional;
import io.pddl.merger.MergeContext;
import io.pddl.merger.resultset.memory.AbstractMemoryResultSet;
import io.pddl.merger.resultset.memory.row.GroupByResultSetRow;
import io.pddl.merger.resultset.memory.row.ResultSetRow;
import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.GroupColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 分组的连接结果集.
 * 
 * @author gaohongtao
 * @author zhangliang
 */
public final class GroupByCouplingResultSet extends AbstractMemoryResultSet {
    
    private final List<GroupColumn> groupByColumns;
    
    private final List<AggregationColumn> aggregationColumns;
    
    private ResultSet resultSet;
    
    private boolean hasNext;
    
    public GroupByCouplingResultSet(final ResultSet resultSet, final MergeContext mc) throws SQLException {
        super(Collections.singletonList(resultSet));
        groupByColumns = mc.getGroupColumns();
        aggregationColumns = mc.getAgregationColumns();
    }
    
    @Override
    protected void initRows(final List<ResultSet> resultSets) throws SQLException {
        resultSet = resultSets.get(0);
        hasNext = resultSet.next();
    }
    
    @Override
    protected Optional<? extends ResultSetRow> nextRow() throws SQLException {
        if (!hasNext) {
            return Optional.absent();
        }
        GroupByResultSetRow result = new GroupByResultSetRow(resultSet, groupByColumns, aggregationColumns);
        List<Object> groupByValues = result.getGroupByValues();
        while (hasNext && (groupByColumns.isEmpty() || groupByValues.equals(result.getGroupByValues()))) {
            result.aggregate();
            hasNext = resultSet.next();
        }
        result.generateResult();
        return Optional.of(result);
    }
}
