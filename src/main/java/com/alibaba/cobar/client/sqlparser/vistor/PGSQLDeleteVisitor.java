package com.alibaba.cobar.client.sqlparser.vistor;

import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.google.common.base.Optional;

public class PGSQLDeleteVisitor extends AbstractPGSQLVisitor {
    
    @Override
    public boolean visit(final PGDeleteStatement x) {
        setCurrentTable(x.getTableName().toString(), Optional.fromNullable(x.getAlias()));
        return super.visit(x);
    }
}