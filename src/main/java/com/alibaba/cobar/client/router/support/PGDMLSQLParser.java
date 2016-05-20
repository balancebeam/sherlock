package com.alibaba.cobar.client.router.support;

import org.apache.commons.lang.StringUtils;

import com.alibaba.cobar.client.router.IDMLSQLParser;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class PGDMLSQLParser implements IDMLSQLParser {

	@Override
	public String getTableName(String sql) {

		if (StringUtils.isEmpty(sql)) {
			return null;
		}

		String tableName = null;

		SQLStatementParser parser = new PGSQLStatementParser(sql);

		SQLStatement statement = parser.parseStatement();

		if (sql.matches("^(?i)\\s*INSERT\\s.+")) {
			tableName = ((PGInsertStatement) statement).getTableName().getSimleName();
		} else if (sql.matches("^(?i)\\s*UPDATE\\s.+")) {
			tableName = ((PGUpdateStatement) statement).getTableName().getSimleName();
		} else if (sql.matches("^(?i)\\s*DELETE\\s.+")) {
			tableName = ((PGDeleteStatement) statement).getTableName().getSimleName();
		}

		return tableName;
	}

}
