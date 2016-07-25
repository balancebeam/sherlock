package io.pddl.merger.resultset.memory;

import io.pddl.jdbc.adapter.AbstractResultSetAdapter;

import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * 声明不支持操作的内存结果集抽象类.
 */
public abstract class AbstractUnsupportedOperationMemoryResultSet extends AbstractResultSetAdapter {
    
    public AbstractUnsupportedOperationMemoryResultSet(final List<ResultSet> resultSets) throws SQLException {
        super(resultSets);
    }
    
    @Override
    public final Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("getObject");
    }
    
    @Override
    public final Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("getObject");
    }
    
    @Override
    public final InputStream getAsciiStream(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getAsciiStream");
    }
    
    @Override
    public final InputStream getAsciiStream(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getAsciiStream");
    }
    
    @Override
    public final InputStream getBinaryStream(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBinaryStream");
    }
    
    @Override
    public final InputStream getBinaryStream(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBinaryStream");
    }
    
    @Override
    public final InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getUnicodeStream");
    }
    
    @Override
    public final InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getUnicodeStream");
    }
    
    @Override
    public final Reader getCharacterStream(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getCharacterStream");
    }
    
    @Override
    public final Reader getCharacterStream(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getCharacterStream");
    }
    
    @Override
    public final Blob getBlob(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBlob");
    }
    
    @Override
    public final Blob getBlob(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBlob");
    }
    
    @Override
    public final Clob getClob(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getClob");
    }
    
    @Override
    public final Clob getClob(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getClob");
    }
    
    @Override
    public final SQLXML getSQLXML(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getSQLXML");
    }
    
    @Override
    public final SQLXML getSQLXML(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getSQLXML");
    }
}
