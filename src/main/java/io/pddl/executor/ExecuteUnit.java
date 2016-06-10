package io.pddl.executor;

public interface ExecuteUnit<I, O> {
    
    O execute(String actualSql,I input) throws Exception;
}
