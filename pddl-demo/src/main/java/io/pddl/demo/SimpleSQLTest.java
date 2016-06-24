package io.pddl.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SimpleSQLTest {

	private static String SQL_STR = "select order_id, user_id, status from t_order where order_id=19 and user_id=3";

	private static Connection conn = null;

	@SuppressWarnings("resource")
	public static void main(String[] args)throws Exception{
		try {
			init();
//			orderSql();
//			groupSqlCount();
//			groupSqlSum();
//			groupSqlMin();
//			groupSqlMax();
//			groupSqlAvg();
//			limitSql();
			ortherSql();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}

	private static void orderSql() throws Exception {
		SQL_STR = "select order_id, user_id, status from t_order where order_id > 19 order by order_id";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================ORDER START==================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id")
					+ ", status|" + rs.getString("status"));
		}
		System.out.println("=================ORDER START==================");
		rs.close();
		statement.close();
	}

	private static void groupSqlCount() throws Exception {
		SQL_STR = "select count(order_id) as order_id, user_id from t_order where order_id <30 group by user_id order by user_id";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================COUNT START==================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id"));
		}
		System.out.println("=================COUNT START==================");
		rs.close();
		statement.close();
	}

	private static void groupSqlSum() throws Exception {
		SQL_STR = "select sum(order_id) as order_id, user_id from t_order where order_id <30 group by user_id order by user_id";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================SUM START==================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id"));
		}
		System.out.println("=================SUM START==================");
		rs.close();
		statement.close();
	}

	private static void groupSqlMax() throws Exception {
		SQL_STR = "select max(order_id) as order_id, user_id from t_order where order_id <30 group by user_id order by user_id";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================MAX START==================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id"));
		}
		System.out.println("=================MAX END==================");
		rs.close();
		statement.close();
	}

	private static void groupSqlMin() throws Exception {
		SQL_STR = "select min(order_id) as order_id, user_id from t_order where order_id <30 group by user_id order by user_id";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================MIN START==================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id"));
		}
		System.out.println("=================MIN END==================");
		rs.close();
		statement.close();
	}

	private static void groupSqlAvg() throws Exception {
		SQL_STR = "select avg(order_id) as order_id, user_id from t_order where order_id <30 group by user_id order by user_id";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================AVG START==================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id"));
		}
		System.out.println("=================AVG END==================");
		rs.close();
		statement.close();
	}


	private static void limitSql() throws Exception {
		SQL_STR = "select order_id, user_id, status from t_order where order_id > 19 order by order_id limit 4 offset 4";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================LIMIT START==================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id")
					+ ", status|" + rs.getString("status"));
		}
		System.out.println("=================LIMIT END==================");
		rs.close();
		statement.close();
	}

	private static void ortherSql() throws Exception {
		SQL_STR = "select o.user_id, o.order_id,i.item_id from t_order o, t_item i where o.order_id=i.order_id and o.order_id in (38,29,40)";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("=================ORTHER START==================");
		while(rs.next()){
			System.out.println("user_id="+rs.getLong("user_id")+",order_id="+rs.getLong("order_id")+",item_id="+rs.getLong("item_id"));
		}
		System.out.println("=================ORTHER END==================");
		rs.close();
		statement.close();
	}

	private static void init() throws Exception {
		final ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		DataSource ds= context.getBean("shardingDataSource", DataSource.class);
		conn= ds.getConnection();
	}
}
