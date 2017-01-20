package io.anyway.sherlock.jdbc.adapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Collection;

import io.anyway.sherlock.jdbc.unsupported.AbstractUnsupportedOperationConnection;

public abstract class AbstractConnectionAdapter extends AbstractUnsupportedOperationConnection {
    
    private boolean autoCommit = true;
    
    private boolean readOnly = false;
    
    private boolean closed;
    
    private int transactionIsolation = TRANSACTION_READ_UNCOMMITTED;
    
    /**
     * 获取Connection代理打开的所有连接，用于事务提交、回滚和关闭等操作
     * @return Collection<Connection>
     */
    protected abstract Collection<Connection> getConnections();
    
    /**
     * 根据数据源名称获取实际的连接，如果开启事务或写操作则每次获取同一个Connection对象，否则获取不同的Connection对象
     * @param dataSourceName 数据源名称
     * @return Connection
     * @throws SQLException
     */
    public abstract Connection getConnection(String dataSourceName) throws SQLException;
    
    @Override
    public final boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }
    
    @Override
    public final void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
        for (Connection each : getConnections()) {
            each.setAutoCommit(autoCommit);
        }
    }
    
    @Override
    public final void commit() throws SQLException {
        for (Connection each : getConnections()) {
            each.commit();
        }
    }
    
    @Override
    public final void rollback() throws SQLException {
        for (Connection each : getConnections()) {
            each.rollback();
        }
    }
    
    @Override
    public final void close() throws SQLException {
        for (Connection each : getConnections()) {
            each.close();
        }
        closed = true;
    }
    
    @Override
    public final boolean isClosed() throws SQLException {
        return closed;
    }
    
    @Override
    public final boolean isReadOnly() throws SQLException {
        return readOnly;
    }
    
    @Override
    public final void setReadOnly(final boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
        for (Connection each : getConnections()) {
            each.setReadOnly(readOnly);
        }
    }
    
    @Override
    public final int getTransactionIsolation() throws SQLException {
        return transactionIsolation;
    }
    
    @Override
    public final void setTransactionIsolation(final int level) throws SQLException {
        transactionIsolation = level;
        for (Connection each : getConnections()) {
            each.setTransactionIsolation(level);
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
    }
    
    @Override
    public final int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }
    
    @Override
    public final void setHoldability(final int holdability) throws SQLException {
    }
}
