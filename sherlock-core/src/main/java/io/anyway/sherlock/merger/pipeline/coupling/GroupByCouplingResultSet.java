package io.anyway.sherlock.merger.pipeline.coupling;

import com.google.common.base.Optional;
import io.anyway.sherlock.merger.resultset.memory.row.GroupByResultSetRow;
import io.anyway.sherlock.merger.resultset.memory.row.ResultSetRow;
import io.anyway.sherlock.sqlparser.bean.AggregationColumn;
import io.anyway.sherlock.sqlparser.bean.GroupColumn;
import io.anyway.sherlock.merger.MergeContext;
import io.anyway.sherlock.merger.resultset.memory.AbstractMemoryResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 分组的连接结果集.
 * 
 * @author xiong.jie
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
