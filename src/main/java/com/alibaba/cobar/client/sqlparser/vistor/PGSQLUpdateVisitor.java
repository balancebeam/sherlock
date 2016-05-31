package com.alibaba.cobar.client.sqlparser.vistor;

import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.google.common.base.Optional;

public class PGSQLUpdateVisitor extends AbstractPGSQLVisitor {
	
    @Override
    public boolean visit(final PGUpdateStatement x) {
        setCurrentTable(x.getTableName().toString(), Optional.<String>absent());
        return super.visit(x);
    }

}