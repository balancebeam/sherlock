package io.anyway.sherlock.sqlparser.visitor.oracle;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.google.common.base.Optional;

public class OracleDeleteVisitor extends AbstractOracleVisitor {
    
    @Override
    public boolean visit(final OracleDeleteStatement x) {
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