package com.hj.cobar.testcase;

import com.alibaba.cobar.client.sqlparser.SQLParseEngine;
import com.alibaba.cobar.client.sqlparser.SQLParsedResult;
import com.alibaba.cobar.client.sqlparser.SQLParserFactory;
import com.alibaba.cobar.client.sqlparser.bean.DatabaseType;
import com.alibaba.cobar.client.sqlparser.bean.Table;
import com.google.common.collect.Lists;

public class sqlmain {

	public static void main(String[] args) {


		 String sql ="SELECT * FROM test1.cont a where a.id=2;"; 
		 SQLParseEngine engine = SQLParserFactory.create(DatabaseType.PG, sql, Lists.newArrayList());
		 SQLParsedResult result = engine.parse();
		 
		 System.out.println(result.getSqlBuilder().toString());
		 
		 for(Table table : result.getTables()){
			 System.out.println(table.toString());
			 result.getSqlBuilder().buildSQL(table.getName(), "cont1");
		 }
		 
		 System.out.println(result.getSqlBuilder().toSQL());
	}

}
