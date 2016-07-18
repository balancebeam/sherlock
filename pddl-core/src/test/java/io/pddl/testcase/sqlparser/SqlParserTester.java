package io.pddl.testcase.sqlparser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.pddl.datasource.DatabaseType;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.SQLParserFactory;
import junit.framework.Assert;
import junit.framework.TestCase;

public class SqlParserTester extends TestCase {
	
	public void testA(){
		String sql= "select name,description,location from emp where id= 2 and name='zhangsan'";
		String actualSql= print("testA",sql,Collections.<Object>emptyList());
//		String expectedSql= "";
//		Assert.assertEquals(expectedSql, actualSql);
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
	public void testK(){
		String sql= "select name, avg(salary) as val ,(select avg(bonus) from emp), max(salary) from t_order o where id in (select id from temp) and hello=12 order by name";
		print("testK",sql,Collections.<Object>emptyList());
	}
	
	public void testL(){
		String sql= "(select avg(salary) as val from emp where id<50) UNION ALL (select avg(salary) as val from emp where id>100)";
		print("testL",sql,Collections.<Object>emptyList());
	}
	
	public void testM(){
		String sql= "select A,B,COUNT(C) from table group by A,B having COUNT(C)>2";
		print("testM",sql,Collections.<Object>emptyList());
	}
	
	public void testN(){
		String sql= "select name  from emp e order by e.dept";
		print("testN",sql,Collections.<Object>emptyList());
	}
	
	public void testO(){
		String sql ="select name from emp where id> 21 limit 7 offset 9";
		print("testO",sql,Collections.<Object>emptyList());
	}
	
	private String print(String topic,String sql,List<Object> parameters){
		SQLParsedResult result= SQLParserFactory.create(DatabaseType.PostgreSQL,sql, parameters).parse();
		System.out.println("---------------"+topic+"---------------------");
		System.out.println(sql);
		System.out.println(result);
		String q= result.getSqlBuilder().toSQL();
		System.out.println(q);
		return q;
	}
}
