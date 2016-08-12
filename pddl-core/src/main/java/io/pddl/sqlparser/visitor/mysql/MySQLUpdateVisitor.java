package io.pddl.sqlparser.visitor.mysql;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;

public class MySQLUpdateVisitor extends AbstractMySQLVisitor {
	
    @Override
    public boolean visit(final MySqlUpdateStatement x) {
       // setCurrentTable(x.getTableName().toString(), Optional.<String>absent());
        return super.visit(x);
    }

}