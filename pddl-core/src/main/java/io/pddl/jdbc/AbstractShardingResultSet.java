package io.pddl.jdbc;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.List;

import org.springframework.util.CollectionUtils;

import io.pddl.jdbc.adapter.AbstractResultSetAdapter;
import io.pddl.merger.Limit;

public abstract class AbstractShardingResultSet extends AbstractResultSetAdapter {
    
    private final Limit limit;
    
    private boolean offsetSkipped;
    
    private int readCount;
    
    protected AbstractShardingResultSet(final List<ResultSet> resultSets, final Limit limit) {
        super(resultSets);
        this.limit = limit;
        if(!CollectionUtils.isEmpty(resultSets)){
        	setCurrentResultSet(resultSets.get(0));
        }
    }
    
    @Override
    public final boolean next() throws SQLException {
        if (null != limit && !offsetSkipped) {
            skipOffset();
        }
        return null == limit ? nextForSharding() : ++readCount <= limit.getRowCount() && nextForSharding();
    }
    
    private void skipOffset() {
        for (int i = 0; i < limit.getOffset(); i++) {
            try {
                if (!nextForSharding()) {
                    break;
                }
            } catch (final SQLException ignored) {
//                log.warn("Skip result set error", ignored);
            }
        }
        offsetSkipped = true;
    }
    
    /**
     * 迭代结果集.
     * 
     * @return true 可以继续访问 false 不能继续访问
     * @throws SQLException
     */
    protected abstract boolean nextForSharding() throws SQLException;
}
