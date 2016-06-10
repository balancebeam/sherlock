package io.pddl.testcase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {

	@SuppressWarnings("resource")
	public static void main(String[] args)throws Exception{
		final ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		DataSource ds= context.getBean("shardingDataSource", DataSource.class);
		Connection conn= ds.getConnection();
		Statement statement= conn.createStatement();
		ResultSet rs= statement.executeQuery("select order_id from t_order where order_id=19 and user_id=3");
		rs.next();
		System.out.println("id= "+rs.getLong(1));
		rs.close();
		statement.close();
		conn.close();
		System.out.println(conn);
	}
}
