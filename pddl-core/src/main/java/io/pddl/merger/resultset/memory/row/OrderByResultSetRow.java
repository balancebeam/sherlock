package io.pddl.merger.resultset.memory.row;

import com.google.common.base.Preconditions;
import io.pddl.merger.util.ResultSetUtil;
import io.pddl.sqlparser.bean.OrderColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 具有排序功能的数据行对象.
 * 
 */
public final class OrderByResultSetRow extends AbstractResultSetRow implements Comparable<OrderByResultSetRow> {
    
    private final List<OrderColumn> orderByColumns;
    
    private final List<Comparable<?>> orderByValues;
    
    public OrderByResultSetRow(final ResultSet resultSet, final List<OrderColumn> orderByColumns) throws SQLException {
        super(resultSet);
        this.orderByColumns = orderByColumns;
        orderByValues = loadOrderByValues();
    }
    
    private List<Comparable<?>> loadOrderByValues() {
        List<Comparable<?>> result = new ArrayList<Comparable<?>>(orderByColumns.size());
        for (OrderColumn each : orderByColumns) {
            Object value = getCell(each.getColumnIndex());
            Preconditions.checkState(value instanceof Comparable, "Sharding-JDBC: order by value must extends Comparable");
            result.add((Comparable<?>) value);
        }
        return result;
    }
    
    @Override
    public int compareTo(final OrderByResultSetRow otherOrderByValue) {
        for (int i = 0; i < orderByColumns.size(); i++) {
            OrderColumn thisOrderColumn = orderByColumns.get(i);
            int result = ResultSetUtil.compareTo(orderByValues.get(i), otherOrderByValue.orderByValues.get(i), thisOrderColumn.getOrderType());
            if (0 != result) {
                return result;
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format("Order by columns value is %s", orderByValues);
    }
}
