package io.anyway.sherlock.merger.pipeline.reducer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

import io.anyway.sherlock.merger.resultset.memory.row.AbstractResultSetRow;
import io.anyway.sherlock.merger.resultset.memory.row.ResultSetRow;
import io.anyway.sherlock.sqlparser.bean.GroupColumn;
import io.anyway.sherlock.merger.MergeContext;
import io.anyway.sherlock.merger.resultset.memory.AbstractMemoryResultSet;

/**
 * 根据GroupBy字段分组结果集.
 *
 * @author xiong.j
 */
public final class GroupByReducerResultSet extends AbstractMemoryResultSet {

    private Map<String, List<GroupByReducerResultSetRow>> mapResultSet;
            
    private Iterator<Map.Entry<String, List<GroupByReducerResultSetRow>>> mapRowIterator;

    private Iterator<GroupByReducerResultSetRow> listRowIterator;

    private List<GroupByReducerResultSetRow> listResultSet;

    private final List<GroupColumn> groupByColumns;

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

        // 根据hash分组数据
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

        // 初始化Iterator
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

    private String getHash(ResultSet resultSet) throws SQLException {
        List<Object> groupByValues = getGroupByValues(resultSet);
        StringBuilder hash = new StringBuilder(groupByValues.size());
        for (Object o : groupByValues) {
            hash.append(o.toString()).append(",");
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


