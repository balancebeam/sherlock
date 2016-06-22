package io.pddl.sqlparser.visitor;

import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;

import io.pddl.sqlparser.bean.Condition.BinaryOperator;

public class PGSQLInsertVisitor  extends AbstractPGSQLVisitor {
    
    @Override
    public boolean visit(final PGInsertStatement x) {
        if (null == x.getValues()) {
            return super.visit(x);
        }
        for (int i = 0; i < x.getColumns().size(); i++) {
        	parseResult.addCondition(x.getColumns().get(i).toString(), x.getTableName().toString(), BinaryOperator.EQUAL, x.getValues().getValues().get(i), getDatabaseType(), getParameters());
        }
        return super.visit(x);
    }
}