package io.pddl.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SimpleSQLTest {

	private static String SQL_STR = "select order_id, user_id, status from t_order where order_id=19 and user_id=3";

	@SuppressWarnings("resource")
	public static void main(String[] args)throws Exception{
		final ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

		SQL_STR = "select order_id, user_id, status from t_order where order_id > 19 order by order_id limit 4";

		DataSource ds= context.getBean("shardingDataSource", DataSource.class);
		Connection conn= ds.getConnection();
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
		conn.close();
		System.out.println(conn);
	}
}
