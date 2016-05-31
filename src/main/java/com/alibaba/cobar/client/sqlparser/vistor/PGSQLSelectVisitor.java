package com.alibaba.cobar.client.sqlparser.vistor;

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.google.common.base.Optional;

public class PGSQLSelectVisitor extends AbstractPGSQLVisitor {
    
    @Override
    public boolean visit(final PGSelectQueryBlock x) {

        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableExpr = (SQLExprTableSource) x.getFrom();
            setCurrentTable(tableExpr.getExpr().toString(), Optional.fromNullable(tableExpr.getAlias()));
            
        }
        return super.visit(x);
    }


}
