package io.anyway.sherlock.exception;
public final class DatabaseTypeUnsupportedException extends ShardingException {
    
    private static final long serialVersionUID = -7807395469148925091L;
    
    private static final String MESSAGE = "Can not support database type [%s].";
    
    public DatabaseTypeUnsupportedException(final String databaseType) {
        super(MESSAGE, databaseType);
    }
}
