package io.pddl.merger.pipeline.reducer;

import io.pddl.merger.MergeContext;
import io.pddl.merger.resultset.delegate.AbstractDelegateResultSet;
import io.pddl.merger.resultset.memory.row.OrderByResultSetRow;
import io.pddl.sqlparser.bean.OrderColumn;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 流式排序的聚集结果集.
 *
 * @author gaohongtao
 * @author zhangliang
 * @author xiong.j
 */
@Slf4j
public final class StreamingOrderByReducerResultSet extends AbstractDelegateResultSet {
    
    private final List<OrderColumn> orderByColumns;
    
    private final List<ResultSet> resultSets = new LinkedList<ResultSet>();
    
    public StreamingOrderByReducerResultSet(final MergeContext mc) throws SQLException {
        super(mc.getResultSets());
        orderByColumns = mc.getOrderColumns();
        resultSets.addAll(mc.getResultSets());
    }
    
    @Override
    protected boolean firstNext() throws SQLException {
        initialResultSetCursors();
        return doNext();
    }
    
    private void initialResultSetCursors() throws SQLException {
        for (ResultSet each : resultSets) {
            each.next();
        }
    }
    
    @Override
    protected boolean afterFirstNext() throws SQLException {
        if (!getDelegate().next()) {
            resultSets.remove(getDelegate());
        }
        return doNext();
    }
    
    private boolean doNext() throws SQLException {
        setDelegateResultSet();
        return !resultSets.isEmpty();
    }
    
    private void setDelegateResultSet() throws SQLException {
        OrderByResultSetRow chosenOrderByValue = null;
        for (ResultSet each : resultSets) {
            OrderByResultSetRow eachOrderByValue = new OrderByResultSetRow(each, orderByColumns);
            if (null == chosenOrderByValue || chosenOrderByValue.compareTo(eachOrderByValue) > 0) {
                chosenOrderByValue = eachOrderByValue;
                setDelegate(each);
            }
        }
        log.trace("Chosen order by value: {}, current result set hashcode: {}", chosenOrderByValue, getDelegate().hashCode());
    }
}
