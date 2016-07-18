package io.pddl.sqlparser.visitor.pgsql;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.google.common.base.Optional;

public class PGSQLDeleteVisitor extends AbstractPGSQLVisitor {
    
    @Override
    public boolean visit(final PGDeleteStatement x) {
        setCurrentTable(x.getTableName().toString(), Optional.fromNullable(x.getAlias()));
        return super.visit(x);
    }
    
    public boolean visit(SQLIdentifierExpr x) {
    	if(x.getParent() instanceof SQLExprTableSource){
    		printToken(x.getName());
    		return false;
    	}
    	return super.visit(x);
    }
}