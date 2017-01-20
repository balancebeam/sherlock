package io.anyway.sherlock.sqlparser.visitor.or;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.google.common.base.Optional;

import io.anyway.sherlock.sqlparser.visitor.or.mysql.OrMySQLVisitor;
import io.anyway.sherlock.sqlparser.visitor.or.oracle.OrOracleVisitor;
import io.anyway.sherlock.sqlparser.visitor.or.pgsql.OrPgsqlVisitor;
import io.anyway.sherlock.sqlparser.SQLParsedResult;

public class OrParser {
    private final SQLStatement sqlStatement;
    
    private final OrVisitor orVisitor;
    
    public OrParser(final SQLStatement sqlStatement, final SQLASTOutputVisitor dependencyVisitor) {
        this.sqlStatement = sqlStatement;
        String dbType= dependencyVisitor.getDbType();
        if (JdbcConstants.ORACLE.equals(dbType)){
        	orVisitor = new OrOracleVisitor(dependencyVisitor);
        }
        else if(JdbcConstants.POSTGRESQL.equals(dbType)){
        	orVisitor = new OrPgsqlVisitor(dependencyVisitor);
        }
        else if(JdbcConstants.MYSQL.equals(dbType)){
            orVisitor = new OrMySQLVisitor(dependencyVisitor);
        }
        else {
            throw new IllegalArgumentException("not support typeof " + dbType + " OrVisitor");
        }
    }
    
    public SQLParsedResult parse() {
        SQLParsedResult result = orVisitor.getSQLResult();
        Optional<AbstractOrASTNode> rootASTNode = orVisitor.visitHandle(sqlStatement);
        if (rootASTNode.isPresent()) {
            result.getConditions().addAll(rootASTNode.get().getCondition());
        }
        return result;
    }
}
