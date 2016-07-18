package io.pddl.jdbc.unsupported;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

import com.alibaba.druid.pool.WrapperAdapter;

public abstract class AbstractUnsupportedOperationStatement extends WrapperAdapter implements Statement {
    
    @Override
    public final int getMaxFieldSize() throws SQLException {
        throw new SQLFeatureNotSupportedException("getMaxFieldSize");
    }
    
    @Override
    public final void setMaxFieldSize(final int max) throws SQLException {
        throw new SQLFeatureNotSupportedException("setMaxFieldSize");
    }
    
    @Override
    public final int getMaxRows() throws SQLException {
        throw new SQLFeatureNotSupportedException("getMaxRows");
    }
    
    @Override
    public final void setMaxRows(final int max) throws SQLException {
        throw new SQLFeatureNotSupportedException("setMaxRows");
    }
    
    @Override
    public final int getQueryTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("getQueryTimeout");
    }
    
    @Override
    public final void setQueryTimeout(final int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("setQueryTimeout");
    }
    
    @Override
    public final int getFetchDirection() throws SQLException {
        throw new SQLFeatureNotSupportedException("getFetchDirection");
    }
    
    @Override
    public final void setFetchDirection(final int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException("setFetchDirection");
    }
    
    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException("getGeneratedKeys");
    }
    
    @Override
    public final void addBatch(final String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("addBatch sql");
    }
    
    @Override
    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("clearBatch");
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("executeBatch");
    }
    
    public final void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException("closeOnCompletion");
    }
    
    public final boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException("isCloseOnCompletion");
    }
}
