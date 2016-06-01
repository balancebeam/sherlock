package com.alibaba.cobar.client.sqlparser.vistor;

import com.alibaba.cobar.client.sqlparser.bean.Condition.BinaryOperator;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.google.common.base.Optional;

public class PGSQLInsertVisitor  extends AbstractPGSQLVisitor {
    
    @Override
    public boolean visit(final PGInsertStatement x) {
        setCurrentTable(x.getTableName().toString(), Optional.fromNullable(x.getAlias()));
        if (null == x.getValues()) {
            return super.visit(x);
        }
        for (int i = 0; i < x.getColumns().size(); i++) {
        	parseResult.addCondition(x.getColumns().get(i).toString(), x.getTableName().toString(), BinaryOperator.EQUAL, x.getValues().getValues().get(i), getDatabaseType(), getParameters());
        }
        return super.visit(x);
    }
}