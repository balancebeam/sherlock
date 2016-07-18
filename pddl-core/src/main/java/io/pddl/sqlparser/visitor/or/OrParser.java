package io.pddl.sqlparser.visitor.or;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.google.common.base.Optional;

import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.visitor.or.oracle.OrOracleVisitor;
import io.pddl.sqlparser.visitor.or.pgsql.OrPgsqlVisitor;

public class OrParser {
    private final SQLStatement sqlStatement;
    
    private final OrVisitor orVisitor;
    
    public OrParser(final SQLStatement sqlStatement, final SQLASTOutputVisitor dependencyVisitor) {
        this.sqlStatement = sqlStatement;
        if (JdbcConstants.ORACLE.equals(dependencyVisitor.getDbType()))
        {
        	orVisitor = new OrOracleVisitor(dependencyVisitor);
        } else {
        	orVisitor = new OrPgsqlVisitor(dependencyVisitor);
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
