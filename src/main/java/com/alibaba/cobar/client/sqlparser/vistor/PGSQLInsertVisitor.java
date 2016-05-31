package com.alibaba.cobar.client.sqlparser.vistor;

import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.google.common.base.Optional;

public class PGSQLInsertVisitor  extends AbstractPGSQLVisitor {
    
    @Override
    public boolean visit(final PGInsertStatement x) {
        setCurrentTable(x.getTableName().toString(), Optional.fromNullable(x.getAlias()));
        return super.visit(x);
    }
}