package io.pddl.sqlparser.visitor.oracle;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;

public class OracleUpdateVisitor extends AbstractOracleVisitor {
	
    @Override
    public boolean visit(final OracleUpdateStatement x) {
       // setCurrentTable(x.getTableName().toString(), Optional.<String>absent());
        return super.visit(x);
    }

}