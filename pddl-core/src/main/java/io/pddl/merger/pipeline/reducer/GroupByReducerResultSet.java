package io.pddl.merger.pipeline.reducer;

import com.google.common.base.Optional;
import io.pddl.merger.MergeContext;
import io.pddl.merger.resultset.memory.AbstractMemoryOrderByResultSet;
import io.pddl.merger.resultset.memory.AbstractMemoryResultSet;
import io.pddl.merger.resultset.memory.row.AbstractResultSetRow;
import io.pddl.merger.resultset.memory.row.GroupByResultSetRow;
import io.pddl.merger.resultset.memory.row.OrderByResultSetRow;
import io.pddl.merger.resultset.memory.row.ResultSetRow;
import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.GroupColumn;
import io.pddl.sqlparser.bean.OrderColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 根据GroupBy字段分组结果集.
 *
 * @author xiong.j
 */
public final class GroupByReducerResultSet extends AbstractMemoryResultSet {

    private Map<String, List<GroupByReducerResultSetRow>> mapResultSet;
            
    private Iterator<Map.Entry<String, List<GroupByReducerResultSetRow>>> mapRowIterator;

    private final List<GroupColumn> groupByColumns;

    private Iterator<GroupByReducerResultSetRow> listRowIterator;

    private List<GroupByReducerResultSetRow> listResultSet;

    public GroupByReducerResultSet(final MergeContext mc) throws SQLException {
        super(mc.getResultSets());
        groupByColumns = mc.getGroupColumns();
    }

    @Override
    protected void initRows(final List<ResultSet> resultSets) throws SQLException {
        // TODO 需换一种容器实现
        mapResultSet = new HashMap<String, List<GroupByReducerResultSetRow>>();
        List<GroupByReducerResultSetRow> orderByResultSetRowList;
        String hash;
        for (ResultSet each : resultSets) {
            while (each.next()) {
                hash = getHash(each);
                if (mapResultSet.containsKey(hash)) {
                    mapResultSet.get(hash).add(new GroupByReducerResultSetRow(each));
                } else {
                    orderByResultSetRowList = new LinkedList<GroupByReducerResultSetRow>();
                    orderByResultSetRowList.add(new GroupByReducerResultSetRow(each));
                    mapResultSet.put(hash, orderByResultSetRowList);
                }
            }
        }
        mapRowIterator = mapResultSet.entrySet().iterator();
        if (mapRowIterator.hasNext()) {
            listResultSet = mapRowIterator.next().getValue();
            listRowIterator = listResultSet.iterator();
        }
    }

    @Override
    protected Optional<? extends ResultSetRow> nextRow() throws SQLException {
        if (listRowIterator.hasNext()) {
            return Optional.of(listRowIterator.next());
        }

        if (mapRowIterator.hasNext()) {
            listResultSet = mapRowIterator.next().getValue();
            listRowIterator = listResultSet.iterator();
            if (listRowIterator.hasNext()) {
                return Optional.of(listRowIterator.next());
            }
        }
        return Optional.absent();
    }

    private Optional<? extends ResultSetRow> innerNext() throws SQLException {
        if (listRowIterator.hasNext()) {
            return Optional.of(listRowIterator.next());
        }
        return Optional.absent();
    }

    private String getHash(ResultSet resultSet) throws SQLException {
        List<Object> groupByValues = getGroupByValues(resultSet);
        StringBuilder hash = new StringBuilder(groupByValues.size());
        for (Object o : groupByValues) {
            hash.append(o.toString());
        }
        return hash.toString();
    }

    private List<Object> getGroupByValues(ResultSet resultSet) throws SQLException {
        List<Object> result = new ArrayList<Object>(groupByColumns.size());
        for (GroupColumn each : groupByColumns) {
            result.add(resultSet.getObject(each.getColumnIndex()));
        }
        return result;
    }

    private class GroupByReducerResultSetRow extends AbstractResultSetRow {

        public GroupByReducerResultSetRow(ResultSet resultSet) throws SQLException {
            super(resultSet);
        }
    }
}


