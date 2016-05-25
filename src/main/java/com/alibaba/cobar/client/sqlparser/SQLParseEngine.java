package com.alibaba.cobar.client.sqlparser;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.cobar.client.exception.SQLParserException;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.google.common.base.Preconditions;

public final class SQLParseEngine {
	protected Log logger = LogFactory.getLog(getClass());
	
    private final SQLStatement sqlStatement;
    
    private final List<Object> parameters;
    
    private final SQLASTOutputVisitor visitor;
    
    
    
    public SQLParseEngine(SQLStatement sqlStatement,List<Object> parameters,SQLASTOutputVisitor visitor){
    	this.sqlStatement = sqlStatement;
    	this.parameters = parameters;
    	this.visitor = visitor;
    }
    
    /**
     *  解析SQL.
     * 
     * @return SQL解析结果
     */
    public SQLParsedResult parse() {
        Preconditions.checkArgument(visitor instanceof SQLVisitor);
        SQLVisitor sqlVisitor = (SQLVisitor) visitor;
        visitor.setParameters(parameters);       
        sqlStatement.accept(visitor);
        
        SQLParsedResult result = sqlVisitor.getSQLResult();
        
        return sqlVisitor.getSQLResult();
    }
    
    private SQLStatementType getType() {
        if (sqlStatement instanceof SQLSelectStatement) {
            return SQLStatementType.SELECT;
        }
        if (sqlStatement instanceof SQLInsertStatement) {
            return SQLStatementType.INSERT;
        }
        if (sqlStatement instanceof SQLUpdateStatement) {
            return SQLStatementType.UPDATE;
        }
        if (sqlStatement instanceof SQLDeleteStatement) {
            return SQLStatementType.DELETE;
        }
        throw new SQLParserException("Unsupported SQL statement: [%s]", sqlStatement);
    }
}