package io.anyway.sherlock.sqlparser.visitor.oracle;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;

import io.anyway.sherlock.sqlparser.bean.Condition.BinaryOperator;

public class OracleInsertVisitor  extends AbstractOracleVisitor {
    
    @Override
    public boolean visit(final OracleInsertStatement x) {
        if (null == x.getValues()) {
            return super.visit(x);
        }
        for (int i = 0; i < x.getColumns().size(); i++) {
        	parseResult.addCondition(x.getColumns().get(i).toString(), x.getTableName().toString(), BinaryOperator.EQUAL, x.getValues().getValues().get(i), getDatabaseType(), getParameters());
        }
        return super.visit(x);
    }
}