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
			//orderSql();
			groupSql();
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
		System.out.println("===================================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id")
					+ ", status|" + rs.getString("status"));
		}
		rs.close();
		statement.close();
	}

	private static void groupSql() throws Exception {
		SQL_STR = "select count(order_id) as order_id, user_id from t_order where order_id <30 group by user_id order by user_id";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("===================================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id"));
		}
		rs.close();
		statement.close();
	}

	private static void limitSql() throws Exception {
		SQL_STR = "select order_id, user_id, status from t_order where order_id > 19 order by order_id limit 4";

		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery(SQL_STR);
		System.out.println("===================================");
		while (rs.next()){
			System.out.println("order_id|" + rs.getString("order_id")
					+ ", user_id|" + rs.getString("user_id")
					+ ", status|" + rs.getString("status"));
		}
		rs.close();
		statement.close();
	}

	private static void init() throws Exception {
		final ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		DataSource ds= context.getBean("shardingDataSource", DataSource.class);
		conn= ds.getConnection();
	}
}
