package io.pddl.merger.resultset.memory;

import com.google.common.base.Optional;
import io.pddl.merger.resultset.memory.row.OrderByResultSetRow;
import io.pddl.merger.resultset.memory.row.ResultSetRow;
import io.pddl.sqlparser.bean.OrderColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于内存排序的结果集抽象类.
 * 
 * @author gaohongtao
 * @author zhangliang
 */
public abstract class AbstractMemoryOrderByResultSet extends AbstractMemoryResultSet {
    
    private final List<OrderColumn> orderByColumns;
    
    private Iterator<OrderByResultSetRow> orderByResultSetRowIterator;
    
    public AbstractMemoryOrderByResultSet(final List<ResultSet> resultSets, final List<OrderColumn> orderByColumns) throws SQLException {
        super(resultSets);
        this.orderByColumns = orderByColumns;
    }
    
    @Override
    protected void initRows(final List<ResultSet> resultSets) throws SQLException {
        List<OrderByResultSetRow> orderByResultSetRows = new LinkedList<OrderByResultSetRow>();
        for (ResultSet each : resultSets) {
            while (each.next()) {
                orderByResultSetRows.add(new OrderByResultSetRow(each, orderByColumns));
            }
        }
        Collections.sort(orderByResultSetRows);
        orderByResultSetRowIterator = orderByResultSetRows.iterator();
    }
    
    @Override
    protected Optional<? extends ResultSetRow> nextRow() throws SQLException {
        if (orderByResultSetRowIterator.hasNext()) {
            return Optional.of(orderByResultSetRowIterator.next());
        }
        return Optional.absent();
    }
}
