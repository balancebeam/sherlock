
package io.pddl.jdbc.adapter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import io.pddl.jdbc.unsupported.AbstractUnsupportedOperationResultSet;

public abstract class AbstractResultSetGetterAdapter extends AbstractUnsupportedOperationResultSet {
    
    private ResultSet currentResultSet;
    
    public void setCurrentResultSet(ResultSet currentResultSet){
    	this.currentResultSet= currentResultSet;
    }
    
    public ResultSet getCurrentResultSet(){
    	return currentResultSet;
    }
    
    @Override
    public final boolean getBoolean(final int columnIndex) throws SQLException {
        return currentResultSet.getBoolean(columnIndex);
    }
    
    @Override
    public final boolean getBoolean(final String columnLabel) throws SQLException {
        return currentResultSet.getBoolean(columnLabel);
    }
    
    @Override
    public final byte getByte(final int columnIndex) throws SQLException {
        return currentResultSet.getByte(columnIndex);
    }
    
    @Override
    public final byte getByte(final String columnLabel) throws SQLException {
        return currentResultSet.getByte(columnLabel);
    }
    
    @Override
    public final short getShort(final int columnIndex) throws SQLException {
        return currentResultSet.getShort(columnIndex);
    }
    
    @Override
    public final short getShort(final String columnLabel) throws SQLException {
        return currentResultSet.getShort(columnLabel);
    }
    
    @Override
    public final int getInt(final int columnIndex) throws SQLException {
        return currentResultSet.getInt(columnIndex);
    }
    
    @Override
    public final int getInt(final String columnLabel) throws SQLException {
        return currentResultSet.getInt(columnLabel);
    }
    
    @Override
    public final long getLong(final int columnIndex) throws SQLException {
        return currentResultSet.getLong(columnIndex);
    }
    
    @Override
    public final long getLong(final String columnLabel) throws SQLException {
        return currentResultSet.getLong(columnLabel);
    }
    
    @Override
    public final float getFloat(final int columnIndex) throws SQLException {
        return currentResultSet.getFloat(columnIndex);
    }
    
    @Override
    public final float getFloat(final String columnLabel) throws SQLException {
        return currentResultSet.getFloat(columnLabel);
    }
    
    @Override
    public final double getDouble(final int columnIndex) throws SQLException {
        return currentResultSet.getDouble(columnIndex);
    }
    
    @Override
    public final double getDouble(final String columnLabel) throws SQLException {
        return currentResultSet.getDouble(columnLabel);
    }
    
    @Override
    public final String getString(final int columnIndex) throws SQLException {
        return currentResultSet.getString(columnIndex);
    }
    
    @Override
    public final String getString(final String columnLabel) throws SQLException {
        return currentResultSet.getString(columnLabel);
    }
    
    @Override
    public final BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return currentResultSet.getBigDecimal(columnIndex);
    }
    
    @Override
    public final BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return currentResultSet.getBigDecimal(columnLabel);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public final BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return currentResultSet.getBigDecimal(columnIndex, scale);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public final BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        return currentResultSet.getBigDecimal(columnLabel, scale);
    }
    
    @Override
    public final byte[] getBytes(final int columnIndex) throws SQLException {
        return currentResultSet.getBytes(columnIndex);
    }
    
    @Override
    public final byte[] getBytes(final String columnLabel) throws SQLException {
        return currentResultSet.getBytes(columnLabel);
    }
    
    @Override
    public final Date getDate(final int columnIndex) throws SQLException {
        return currentResultSet.getDate(columnIndex);
    }
    
    @Override
    public final Date getDate(final String columnLabel) throws SQLException {
        return currentResultSet.getDate(columnLabel);
    }
    
    @Override
    public final Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return currentResultSet.getDate(columnIndex, cal);
    }
    
    @Override
    public final Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        return currentResultSet.getDate(columnLabel, cal);
    }
    
    @Override
    public final Time getTime(final int columnIndex) throws SQLException {
        return currentResultSet.getTime(columnIndex);
    }
    
    @Override
    public final Time getTime(final String columnLabel) throws SQLException {
        return currentResultSet.getTime(columnLabel);
    }
    
    @Override
    public final Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return currentResultSet.getTime(columnIndex, cal);
    }
    
    @Override
    public final Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        return currentResultSet.getTime(columnLabel, cal);
    }
    
    @Override
    public final Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return currentResultSet.getTimestamp(columnIndex);
    }
    
    @Override
    public final Timestamp getTimestamp(final String columnLabel) throws SQLException {
        return currentResultSet.getTimestamp(columnLabel);
    }
    
    @Override
    public final Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return currentResultSet.getTimestamp(columnIndex, cal);
    }
    
    @Override
    public final Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        return currentResultSet.getTimestamp(columnLabel, cal);
    }
    
    @Override
    public final InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return currentResultSet.getAsciiStream(columnIndex);
    }
    
    @Override
    public final InputStream getAsciiStream(final String columnLabel) throws SQLException {
        return currentResultSet.getAsciiStream(columnLabel);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public final InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return currentResultSet.getUnicodeStream(columnIndex);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public final InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        return currentResultSet.getUnicodeStream(columnLabel);
    }
    
    @Override
    public final InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return currentResultSet.getBinaryStream(columnIndex);
    }
    
    @Override
    public final InputStream getBinaryStream(final String columnLabel) throws SQLException {
        return currentResultSet.getBinaryStream(columnLabel);
    }
    
    @Override
    public final Reader getCharacterStream(final int columnIndex) throws SQLException {
        return currentResultSet.getCharacterStream(columnIndex);
    }
    
    @Override
    public final Reader getCharacterStream(final String columnLabel) throws SQLException {
        return currentResultSet.getCharacterStream(columnLabel);
    }
    
    @Override
    public final Blob getBlob(final int columnIndex) throws SQLException {
        return currentResultSet.getBlob(columnIndex);
    }
    
    @Override
    public final Blob getBlob(final String columnLabel) throws SQLException {
        return currentResultSet.getBlob(columnLabel);
    }
    
    @Override
    public final Clob getClob(final int columnIndex) throws SQLException {
        return currentResultSet.getClob(columnIndex);
    }
    
    @Override
    public final Clob getClob(final String columnLabel) throws SQLException {
        return currentResultSet.getClob(columnLabel);
    }
    
    @Override
    public final URL getURL(final int columnIndex) throws SQLException {
        return currentResultSet.getURL(columnIndex);
    }
    
    @Override
    public final URL getURL(final String columnLabel) throws SQLException {
        return currentResultSet.getURL(columnLabel);
    }
    
    @Override
    public final SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return currentResultSet.getSQLXML(columnIndex);
    }
    
    @Override
    public final SQLXML getSQLXML(final String columnLabel) throws SQLException {
        return currentResultSet.getSQLXML(columnLabel);
    }
    
    @Override
    public final Object getObject(final int columnIndex) throws SQLException {
        return currentResultSet.getObject(columnIndex);
    }
    
    @Override
    public final Object getObject(final String columnLabel) throws SQLException {
        return currentResultSet.getObject(columnLabel);
    }
    
    @Override
    public final Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        return currentResultSet.getObject(columnIndex, map);
    }
    
    @Override
    public final Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return currentResultSet.getObject(columnLabel, map);
    }
}
