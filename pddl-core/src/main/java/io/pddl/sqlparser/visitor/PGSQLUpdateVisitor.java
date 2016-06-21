package io.pddl.sqlparser.visitor;

import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;

public class PGSQLUpdateVisitor extends AbstractPGSQLVisitor {
	
    @Override
    public boolean visit(final PGUpdateStatement x) {
       // setCurrentTable(x.getTableName().toString(), Optional.<String>absent());
        return super.visit(x);
    }

}