package io.pddl.exception;
public final class SQLParserException extends ShardingException {
    
    private static final long serialVersionUID = -1498980479829506655L;
    
    public SQLParserException(final String message, final Object... args) {
        super(message, args);
    }
}