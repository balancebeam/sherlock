package io.pddl.sqlparser.visitor.mysql;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import io.pddl.sqlparser.bean.Condition.BinaryOperator;

public class MySQLInsertVisitor extends AbstractMySQLVisitor {
    
    @Override
    public boolean visit(final MySqlInsertStatement x) {
        if (null == x.getValues()) {
            return super.visit(x);
        }
        for (int i = 0; i < x.getColumns().size(); i++) {
        	parseResult.addCondition(x.getColumns().get(i).toString(), x.getTableName().toString(), BinaryOperator.EQUAL, x.getValues().getValues().get(i), getDatabaseType(), getParameters());
        }
        return super.visit(x);
    }
}