package io.pddl.sqlparser.visitor.or;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.google.common.base.Optional;

import io.pddl.sqlparser.SQLParsedResult;

public class OrParser {
    private final SQLStatement sqlStatement;
    
    private final OrVisitor orVisitor;
    
    public OrParser(final SQLStatement sqlStatement, final SQLASTOutputVisitor dependencyVisitor) {
        this.sqlStatement = sqlStatement;
        orVisitor = new OrVisitor(dependencyVisitor);
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
