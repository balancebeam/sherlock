package io.pddl.jdbc.adapter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;

public abstract class AbstractResultSetAdapter extends AbstractResultSetGetterAdapter {
    
    private final List<ResultSet> resultSets;
    
    public AbstractResultSetAdapter(List<ResultSet> resultSets){
    	this.resultSets= resultSets;
    }
    
    public List<ResultSet> getResultSets(){
    	return resultSets;
    }
    
    private boolean closed;
    
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
    public final boolean wasNull() throws SQLException {
        return getCurrentResultSet().wasNull();
    }
    
    @Override
    public final int getFetchDirection() throws SQLException {
        return getCurrentResultSet().getFetchDirection();
    }
    
    @Override
    public final void setFetchDirection(final int direction) throws SQLException {
        for (ResultSet each : resultSets) {
            each.setFetchDirection(direction);
        }
    }
    
    @Override
    public final int getFetchSize() throws SQLException {
        return getCurrentResultSet().getFetchSize();
    }
    
    @Override
    public final void setFetchSize(final int rows) throws SQLException {
        for (ResultSet each : resultSets) {
            each.setFetchSize(rows);
        }
    }
    
    @Override
    public final int getType() throws SQLException {
        return getCurrentResultSet().getType();
    }
    
    @Override
    public final int getConcurrency() throws SQLException {
        return getCurrentResultSet().getConcurrency();
    }
    
    @Override
    public final Statement getStatement() throws SQLException {
        return getCurrentResultSet().getStatement();
    }
    
    @Override
    public final SQLWarning getWarnings() throws SQLException {
        return getCurrentResultSet().getWarnings();
    }
    
    @Override
    public final void clearWarnings() throws SQLException {
        getCurrentResultSet().clearWarnings();
    }
    
    @Override
    public final ResultSetMetaData getMetaData() throws SQLException {
        return getCurrentResultSet().getMetaData();
    }
    
    @Override
    public final int findColumn(final String columnLabel) throws SQLException {
        return getCurrentResultSet().findColumn(columnLabel);
    }
}
