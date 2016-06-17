package io.pddl.jdbc.adapter;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Collection;

import io.pddl.jdbc.unsupported.AbstractUnsupportedOperationStatement;

public abstract class AbstractStatementAdapter extends AbstractUnsupportedOperationStatement {
    
    private boolean closed;
    
    private boolean poolable;
    
    private int fetchSize;
    
    @Override
    public final void close() throws SQLException {
        for (Statement each : getRoutedStatements()) {
            each.close();
        }
        closed = true;
        clearRoutedStatements();
    }
    
    @Override
    public final boolean isClosed() throws SQLException {
        return closed;
    }
    
    @Override
    public final boolean isPoolable() throws SQLException {
        return poolable;
    }
    
    @Override
    public final void setPoolable(final boolean poolable) throws SQLException {
        this.poolable = poolable;
        for (Statement each : getRoutedStatements()) {
            each.setPoolable(poolable);
        }
    }
    
    @Override
    public final int getFetchSize() throws SQLException {
        return fetchSize;
    }
    
    @Override
    public final void setFetchSize(final int rows) throws SQLException {
        this.fetchSize = rows;
        for (Statement each : getRoutedStatements()) {
            each.setFetchSize(rows);
        }
    }
    
    @Override
    public final void setEscapeProcessing(final boolean enable) throws SQLException {
        for (Statement each : getRoutedStatements()) {
            each.setEscapeProcessing(enable);
        }
    }
    
    @Override
    public final void cancel() throws SQLException {
        for (Statement each : getRoutedStatements()) {
            each.cancel();
        }
    }
    
    @Override
    public final void setCursorName(final String name) throws SQLException {
        for (Statement each : getRoutedStatements()) {
            each.setCursorName(name);
        }
    }
    
    @Override
    public final int getUpdateCount() throws SQLException {
        int result = 0;
        for (Statement each : getRoutedStatements()) {
            result += each.getUpdateCount();
        }
        return result;
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
    }
    
    /* 
     * 只有存储过程会出现多结果集, 因此不支持.
     */
    @Override
    public final boolean getMoreResults() throws SQLException {
        return false;
    }
    
    @Override
    public final boolean getMoreResults(final int current) throws SQLException {
        return false;
    }
    
    /**
     * 获取路由的静态语句对象集合.
     * 
     * @return 路由的静态语句对象集合
     * @throws SQLException
     */
    protected abstract Collection<? extends Statement> getRoutedStatements() throws SQLException;
    
    /**
     *  清除路由的静态语句对象集合.
     *
     * @throws SQLException
     */
    public abstract void clearRoutedStatements() throws SQLException;
}
