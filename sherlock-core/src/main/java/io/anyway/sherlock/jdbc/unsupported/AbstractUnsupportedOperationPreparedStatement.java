package io.anyway.sherlock.jdbc.unsupported;

import java.io.Reader;
import java.sql.Array;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import io.anyway.sherlock.jdbc.ShardingConnection;
import io.anyway.sherlock.jdbc.ShardingStatement;

public abstract class AbstractUnsupportedOperationPreparedStatement extends ShardingStatement implements PreparedStatement {
    
    protected AbstractUnsupportedOperationPreparedStatement(final ShardingConnection shardingConnection, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
        super(shardingConnection, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public final ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException("getMetaData");
    }
    
    @Override
    public final ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException("ParameterMetaData");
    }
    
    @Override
    public final void setNString(final int parameterIndex, final String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNString");
    }
    
    @Override
    public final void setNClob(final int parameterIndex, final NClob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNClob");
    }
    
    @Override
    public final void setNClob(final int parameterIndex, final Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNClob");
    }
    
    @Override
    public final void setNClob(final int parameterIndex, final Reader x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNClob");
    }
    
    @Override
    public final void setNCharacterStream(final int parameterIndex, final Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNCharacterStream");
    }
    
    @Override
    public final void setNCharacterStream(final int parameterIndex, final Reader x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNCharacterStream");
    }
    
    @Override
    public final void setArray(final int parameterIndex, final Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setArray");
    }
    
    @Override
    public final void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setRowId");
    }
}
