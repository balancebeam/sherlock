package io.pddl.jdbc.adapter;

import com.google.common.base.Preconditions;
import io.pddl.jdbc.unsupported.AbstractUnsupportedOperationResultSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 代理结果集适配器.
 * 
 * @author zhangliang
 */
@Slf4j
public abstract class AbstractResultSetAdapter extends AbstractUnsupportedOperationResultSet {
    
    @Getter(AccessLevel.PROTECTED)
    private final List<ResultSet> resultSets;
    
    @Getter
    private final Map<String, Integer> columnLabelIndexMap;

    private boolean closed;
    
    public AbstractResultSetAdapter(final List<ResultSet> resultSets) throws SQLException {
        Preconditions.checkArgument(!resultSets.isEmpty());
        this.resultSets = resultSets;
        columnLabelIndexMap = generateColumnLabelIndexMap();
    }
    
    private Map<String, Integer> generateColumnLabelIndexMap() throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSets.get(0).getMetaData();
        Map<String, Integer> result = new CaseInsensitiveMap<String, Integer>(resultSetMetaData.getColumnCount());
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            result.put(resultSetMetaData.getColumnLabel(i), i);
        }
        return result;
    }
    
    @Override
    public final void close() throws SQLException {
        for (ResultSet each : resultSets) {
            each.close();
        }
        closed = true;
    }
    
    @Override
    public final boolean isClosed() throws SQLException {
        return closed;
    }
    
    @Override
    public final void setFetchDirection(final int direction) throws SQLException {
        for (ResultSet each : resultSets) {
            each.setFetchDirection(direction);
        }
    }
    
    @Override
    public final void setFetchSize(final int rows) throws SQLException {
        for (ResultSet each : resultSets) {
            each.setFetchSize(rows);
        }
    }
}
