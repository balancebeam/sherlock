package io.anyway.sherlock.sqlparser.visitor.mysql;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.google.common.base.Optional;

public class MySQLDeleteVisitor extends AbstractMySQLVisitor {
    
    @Override
    public boolean visit(final MySqlDeleteStatement x) {
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