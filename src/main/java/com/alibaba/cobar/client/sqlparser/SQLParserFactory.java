package com.alibaba.cobar.client.sqlparser;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.cobar.client.util.ClassUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import io.pddl.datasource.DatabaseType;
import io.pddl.exception.SQLParserException;
import io.pddl.exception.ShardingTableException;

public final class SQLParserFactory {
	private static Log logger = LogFactory.getLog(SQLParserFactory.class);
	
    /**
     * 创建解析器引擎对象.
     * 
     * @param databaseType 数据库类型
     * @param sql SQL语句
     * @param parameters SQL中参数的值
     * @param shardingColumns 分片列名称集合
     * @return 解析器引擎对象
     * @throws SQLParserException SQL解析异常
     */
    public static SQLParseEngine create(final String sql, final List<Object> parameters) throws SQLParserException {
    	logger.debug("Logic SQL: "+ sql);
        SQLStatement sqlStatement = getSQLStatementParser(sql).parseStatement();
        
        logger.debug("Get "+ sqlStatement.getClass().getName()+" SQL Statement");
        return new SQLParseEngine(sqlStatement, parameters, getSQLVisitor(sqlStatement));
    }
    
    public static String getDMLTableName(final String sql){
    	 SQLStatement sqlStatement = getSQLStatementParser(sql).parseStatement();
    	 if(sqlStatement instanceof SQLInsertStatement){
    		 return ((SQLInsertStatement)sqlStatement).getTableName().getSimleName();
    	 }
    	 if(sqlStatement instanceof SQLUpdateStatement){
    		 return ((SQLUpdateStatement)sqlStatement).getTableName().getSimleName();
    	 }
    	 if(sqlStatement instanceof SQLDeleteStatement){
    		 return ((SQLDeleteStatement)sqlStatement).getTableName().getSimleName();
    	 }
    	 return null;
    }
    
    private static SQLStatementParser getSQLStatementParser(final String sql) {
    	DatabaseType databaseType = DatabaseType.getApplicationDatabaseType();
        switch (databaseType) {
            case MySQL: 
                return new MySqlStatementParser(sql);
            case PostgreSQL:
            	return new PGSQLStatementParser(sql);
            default: 
                throw new UnsupportedOperationException(String.format("Cannot support database type [%s]", databaseType));
        }
    }
    
    private static SQLASTOutputVisitor getSQLVisitor(final SQLStatement sqlStatement) {
    	DatabaseType databaseType= DatabaseType.getApplicationDatabaseType();
        if (sqlStatement instanceof SQLSelectStatement) {
            return ClassUtil.newInstance(SQLVisitorRegistry.getSelectVistor(databaseType));
        }
        if (sqlStatement instanceof SQLInsertStatement) {
            return ClassUtil.newInstance(SQLVisitorRegistry.getInsertVistor(databaseType));
        }
        if (sqlStatement instanceof SQLUpdateStatement) {
            return ClassUtil.newInstance(SQLVisitorRegistry.getUpdateVistor(databaseType));
        }
        if (sqlStatement instanceof SQLDeleteStatement) {
            return ClassUtil.newInstance(SQLVisitorRegistry.getDeleteVistor(databaseType));
        }
        throw new SQLParserException("Unsupported SQL statement: [%s]", sqlStatement);
    }
}