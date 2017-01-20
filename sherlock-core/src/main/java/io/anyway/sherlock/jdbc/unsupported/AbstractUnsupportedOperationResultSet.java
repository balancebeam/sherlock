package io.anyway.sherlock.jdbc.unsupported;

import java.io.Reader;
import java.sql.Array;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public abstract class AbstractUnsupportedOperationResultSet extends AbstractResultSetUpdaterAdapter {
    
    @Override
    public final boolean previous() throws SQLException {
        throw new SQLFeatureNotSupportedException("previous");
    }
    
    @Override
    public final boolean isBeforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("isBeforeFirst");
    }
    
    @Override
    public final boolean isAfterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("isAfterLast");
    }
    
    @Override
    public final boolean isFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("isFirst");
    }
    
    @Override
    public final boolean isLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("isLast");
    }
    
    @Override
    public final void beforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("beforeFirst");
    }
    
    @Override
    public final void afterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("afterLast");
    }
    
    @Override
    public final boolean first() throws SQLException {
        throw new SQLFeatureNotSupportedException("first");
    }
    
    @Override
    public final boolean last() throws SQLException {
        throw new SQLFeatureNotSupportedException("last");
    }
    
    @Override
    public final boolean absolute(final int row) throws SQLException {
        throw new SQLFeatureNotSupportedException("absolute");
    }
    
    @Override
    public final boolean relative(final int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException("relative");
    }
    
    @Override
    public final int getRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("getRow");
    }
    
    @Override
    public final void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("insertRow");
    }
    
    @Override
    public final void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("updateRow");
    }
    
    @Override
    public final void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("deleteRow");
    }
    
    @Override
    public final void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("refreshRow");
    }
    
    @Override
    public final void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException("cancelRowUpdates");
    }
    
    @Override
    public final void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("moveToInsertRow");
    }
    
    @Override
    public final void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("moveToCurrentRow");
    }
    
    @Override
    public final boolean rowInserted() throws SQLException {
        throw new SQLFeatureNotSupportedException("rowInserted");
    }
    
    @Override
    public final boolean rowUpdated() throws SQLException {
        throw new SQLFeatureNotSupportedException("rowUpdated");
    }
    
    @Override
    public final boolean rowDeleted() throws SQLException {
        throw new SQLFeatureNotSupportedException("rowDeleted");
    }
    
    @Override
    public final String getCursorName() throws SQLException {
        throw new SQLFeatureNotSupportedException("getCursorName");
    }
    
    @Override
    public final int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("getHoldability");
    }
    
    @Override
    public final String getNString(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getNString");
    }
    
    @Override
    public final String getNString(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getNString");
    }
    
    @Override
    public final NClob getNClob(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getNClob");
    }
    
    @Override
    public final NClob getNClob(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getNClob");
    }
    
    @Override
    public final Reader getNCharacterStream(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getNCharacterStream");
    }
    
    @Override
    public final Reader getNCharacterStream(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getNCharacterStream");
    }
    
    @Override
    public final Ref getRef(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getRef");
    }
    
    @Override
    public final Ref getRef(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getRef");
    }
    
    @Override
    public final Array getArray(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getArray");
    }
    
    @Override
    public final Array getArray(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getArray");
    }
    
    @Override
    public final RowId getRowId(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getRowId");
    }
    
    @Override
    public final RowId getRowId(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getRowId");
    }
    
    public final <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException("getObject with type");
    }
    
    public final <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException("getObject with type");
    }
}
