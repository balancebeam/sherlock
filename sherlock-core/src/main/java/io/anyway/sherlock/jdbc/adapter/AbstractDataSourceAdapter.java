package io.anyway.sherlock.jdbc.adapter;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import io.anyway.sherlock.jdbc.unsupported.AbstractUnsupportedOperationDataSource;

public abstract class AbstractDataSourceAdapter extends AbstractUnsupportedOperationDataSource {
    
    private PrintWriter logWriter = new PrintWriter(System.out);
    
    @Override
    public final PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }
    
    @Override
    public final void setLogWriter(final PrintWriter out) throws SQLException {
        this.logWriter = out;
    }
    
    public final Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
}
