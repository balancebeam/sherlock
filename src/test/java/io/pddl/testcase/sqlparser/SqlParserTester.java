package io.pddl.testcase.sqlparser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.SQLParserFactory;
import junit.framework.TestCase;

public class SqlParserTester extends TestCase {
	/*
	public void testA(){
		String sql= "select name,description,location from emp where id= 2 and name='zhangsan'";
		print("testA",sql,Collections.<Object>emptyList());
	}
	
	public void testB(){
		String sql= "select t.name,t.descritpion,t.location from emp t where t.id= 2 and t.name='zhangsan'";
		print("testB",sql,Collections.<Object>emptyList());
	}
	
	public void testC(){
		String sql= "select t.name as name,t.descritpion describ,t.location loc from emp t where t.id= 2 and t.name='zhangsan'";
		print("testC",sql,Collections.<Object>emptyList());
	}
	
	public void testD(){
		String sql= "select t.name,t.descritpion,t.location from emp t where t.id= ? and t.name=?";
		print("testD",sql,Arrays.<Object>asList(new Object[]{2,"zhangsan"}));
	}
	
	public void testE(){
		String sql= "select o.order_name,i.item_name from t_order o,t_item i where o.order_id= i.order_id and o.user_id=?";
		print("testE",sql,Arrays.<Object>asList(new Object[]{26}));
	}
	
	public void testF(){
		String sql= "select o.order_name,i.item_name from t_order o,t_item i where o.order_id= i.order_id and o.user_id in (?,?,?)";
		print("testF",sql,Arrays.<Object>asList(new Object[]{26,45,76}));
	}
	
	public void testG(){
		String sql= "select o.order_name,i.item_name from t_order o,t_item i where o.order_id= i.order_id and o.user_id between ? and ?";
		print("testG",sql,Arrays.<Object>asList(new Object[]{26,37}));
	}
	
	public void testH(){
		String sql= "select o.order_name,i.item_name from t_order o,t_item i where o.order_id= i.order_id and (o.user_id=? or o.user_id=?)";
		print("testH",sql,Arrays.<Object>asList(new Object[]{26,37}));
	}
	
	public void testI(){
		String sql= "select o.order_name,i.item_name from t_order o,t_item i where (o.order_id=? and o.user_id=?) or (o.order_id=? and o.user_id=?)";
		print("testI",sql,Arrays.<Object>asList(new Object[]{26,37,45,67}));
	}
	
	public void testJ(){
		String sql= "select o.order_name ,o.salary  from t_order o order by 1 asc, o.salary desc";
		print("testJ",sql,Arrays.<Object>asList(new Object[]{26,37,45,67}));
	}
	*/
	
	public void testK(){
		String sql= "select avg(salary) as v ,max(salary) from t_order o";
		print("testK",sql,Collections.<Object>emptyList());
		sql= "select avg(salary) as v ,(select avg(bonus) from emp), max(salary) from t_order o";
		print("testK",sql,Collections.<Object>emptyList());
	}
	
	private void print(String topic,String sql,List<Object> parameters){
		SQLParsedResult result= SQLParserFactory.create(sql, parameters).parse();
		System.out.println("---------------"+topic+"---------------------");
		System.out.println("tables= "+result.getTables());
		System.out.println("conditions= "+result.getCondition().getAllCondition());
		System.out.println("selectItems= "+result.getSelectItems());
		System.out.println("orderColumns= "+result.getOrderColumns());
		System.out.println("sql= "+result.getSqlBuilder());
	}
}
