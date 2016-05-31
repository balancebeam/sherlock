package com.alibaba.cobar.client.sqlparser;

import java.util.Set;

import com.alibaba.cobar.client.sqlparser.bean.Table;
import com.google.common.collect.Sets;

public class SQLParsedResult {

	private SQLBuilder sqlBuilder;
	private Set<Table> tables;
	
	public SQLParsedResult(SQLBuilder sqlBuilder){
		this.sqlBuilder = sqlBuilder;
		this.tables = Sets.newHashSet();
	}
	
	public SQLBuilder getSqlBuilder() {
		return sqlBuilder;
	}
	
	public void setSqlBuilder(SQLBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}
	
	public Set<Table> getTables() {
		return tables;
	}
	
	public Table addTable(Table table){
		this.tables.add(table);
		return table;
	}
	
	
}
