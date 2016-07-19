package io.pddl.sqlparser;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import io.pddl.datasource.DatabaseType;
import io.pddl.exception.SQLParserException;
import io.pddl.util.ClassUtil;

public final class SQLParserFactory {
	private static Log logger = LogFactory.getLog(SQLParserFactory.class);
	
    /**
     * 创建解析器引擎对象.
     * 
     * @param databaseType 数据库类型
     * @param sql SQL语句
     * @param parameters SQL中参数的值
     * @return 解析器引擎对象
     * @throws SQLParserException SQL解析异常
     */
    public static SQLParseEngine create(DatabaseType databaseType,final String sql, final List<Object> parameters) throws SQLParserException {
    	logger.debug("Logic SQL: "+ sql);
        SQLStatement sqlStatement = getSQLStatementParser(databaseType,sql).parseStatement();
        
        logger.debug("Get "+ sqlStatement.getClass().getName()+" SQL Statement");
        SQLASTOutputVisitor visitor= getSQLVisitor(databaseType,sqlStatement);
        //注入原始的SQL语句
        if(visitor instanceof SQLAware){
        	((SQLAware)visitor).setSQL(sql);
        }
        return new SQLParseEngine(sqlStatement,parameters, visitor);
    }
    
    private static SQLStatementParser getSQLStatementParser(DatabaseType databaseType,final String sql) {
        switch (databaseType) {
            case MySQL: 
                return new MySqlStatementParser(sql);
            case PostgreSQL:
            	return new PGSQLStatementParser(sql);
            case Oracle:
            	return new OracleStatementParser(sql);
            default: 
                throw new UnsupportedOperationException(String.format("Cannot support database type [%s]", databaseType));
        }
    }
    
    private static SQLASTOutputVisitor getSQLVisitor(DatabaseType databaseType,SQLStatement sqlStatement) {
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