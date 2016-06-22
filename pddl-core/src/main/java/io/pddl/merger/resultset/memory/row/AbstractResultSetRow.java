package io.pddl.merger.resultset.memory.row;

import com.google.common.base.Preconditions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 结果集数据行抽象类.
 * 
 * @author gaohongtao
 * @author zhangliang
 */
public abstract class AbstractResultSetRow implements ResultSetRow {
    
    private final Object[] rowData;
    
    public AbstractResultSetRow(final ResultSet resultSet) throws SQLException {
        rowData = loadData(resultSet);
    }
    
    private Object[] loadData(final ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        Object[] result = new Object[resultSetMetaData.getColumnCount()];
        for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            result[i] = resultSet.getObject(i + 1);
        }
        return result;
    }
    
    @Override
    public final void setCell(final int columnIndex, final Object value) {
        Preconditions.checkArgument(inRange(columnIndex));
        rowData[columnIndex - 1] = value;
    }
    
    @Override
    public final Object getCell(final int columnIndex) {
        Preconditions.checkArgument(inRange(columnIndex));
        return rowData[columnIndex - 1];
    }
    
    @Override
    public final boolean inRange(final int columnIndex) {
        return columnIndex > 0 && columnIndex < rowData.length + 1;
    }
}
