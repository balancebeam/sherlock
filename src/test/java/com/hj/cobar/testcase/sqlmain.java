package com.hj.cobar.testcase;

import java.util.List;

import com.alibaba.cobar.client.sqlparser.SQLParseEngine;
import com.alibaba.cobar.client.sqlparser.SQLParsedResult;
import com.alibaba.cobar.client.sqlparser.SQLParserFactory;
import com.alibaba.cobar.client.sqlparser.bean.Condition;
import com.alibaba.cobar.client.sqlparser.bean.DatabaseType;
import com.alibaba.cobar.client.sqlparser.bean.Table;
import com.google.common.collect.Lists;

public class sqlmain {

	public static void main(String[] args) {


		 String sql ="SELECT * FROM test1.cont a where a.id=? or bbb=? and a.name=? or a.sex=nam"; 
		 List<Object> list = Lists.newArrayList();
		 list.add(777);
		 list.add(9);
		 list.add(2);
		 
		 SQLParseEngine engine = SQLParserFactory.create(DatabaseType.POSTGRESQL, sql, list);
		 SQLParsedResult result = engine.parse();
		 
		 System.out.println(result.getSqlBuilder().toString());
		 
		 for(Table table : result.getTables()){
			 System.out.println(table.toString());
			 result.getSqlBuilder().buildSQL(table.getName(), "cont1");
		 }
		 
		 for(Condition cond :result.getCondition().getAllCondition()){
			 System.out.println(cond);
		 }
		 
		 
		 System.out.println(result.getSqlBuilder().toSQL());
	}

}
