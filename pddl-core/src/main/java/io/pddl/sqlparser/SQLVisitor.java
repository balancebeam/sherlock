package io.pddl.sqlparser;

import io.pddl.datasource.DatabaseType;

public interface SQLVisitor {
    
    /**
     * 获取数据库类型.
     * 
     * @return 数据库类型
     */
    DatabaseType getDatabaseType();
    
    
    /**
     * 获取SQL构建器.
     * 
     * @return SQL构建器
     */
    SQLParsedResult getSQLResult();
    
    /**
     * 打印替换标记.
     * 
     * @param token 替换标记
     */
    void printToken(String token);
}
