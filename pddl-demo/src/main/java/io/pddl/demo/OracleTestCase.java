package io.pddl.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.pddl.hint.HintContext;
import io.pddl.hint.HintContextHolder;
import io.pddl.sequence.SequenceGenerator;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:oracle/applicationContext.xml")
public class OracleTestCase extends TestCase{

	@Autowired
	@Qualifier("shardingOracleDataSource")
	private DataSource shardingDataSource;
	
	@Resource(name="testSequence")
	private SequenceGenerator sequence;
	
	@Test
	public void testTenant() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		
		String sql= "select order_id from t_order t order by t.user_id";
		Statement st= conn.createStatement();
		ResultSet rs= st.executeQuery(sql);
		while(rs.next()){
			System.out.println("order_id="+rs.getString(1));
		}
		rs.close();
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testOnlyAggregation() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		
		String sql= "select max(order_id) oMax, min(order_id) oMin, sum(order_id) oSum, count(order_id) oCount, avg(order_id) oAvg from t_order";
		Statement st= conn.createStatement();
		ResultSet rs= st.executeQuery(sql);
		while(rs.next()){
			System.out.println("oMax="+rs.getLong("oMax")+
					", oMin="+rs.getLong(3)+
					", oSum="+rs.getLong("oSum")+
					", oCount="+rs.getLong("oCount")+
					", oAvg="+rs.getDouble("oAvg"));
		}
		rs.close();
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testOnlyGroupby() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		String sql= "select user_id,status from t_order group by user_id,status";
		Statement st= conn.createStatement();
		ResultSet rs= st.executeQuery(sql);
		while(rs.next()){
			System.out.println("user_id="+rs.getLong("user_id")+",status="+rs.getString("status"));
		}
		rs.close();
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testGroupbyAndAggregation() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		
		String sql= "select user_id,max(order_id) oMax, min(order_id) oMin, sum(order_id) oSum, count(order_id) oCount, avg(order_id) oAvg from t_order group by user_id";
		Statement st= conn.createStatement();
		ResultSet rs= st.executeQuery(sql);
		while(rs.next()){
			System.out.println("user_id="+rs.getString("user_id")+
					", oMax="+rs.getLong("oMax")+
					", oMin="+rs.getLong(3)+
					", oSum="+rs.getLong("oSum")+
					", oCount="+rs.getLong("oCount")+
					", oAvg="+rs.getDouble("oAvg"));
		}
		rs.close();
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testERNoCondition() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		String sql= "select o.user_id, o.order_id,i.item_id from t_order o, t_item i where o.order_id=i.order_id";
		Statement st= conn.createStatement();
		try{
			ResultSet rs= st.executeQuery(sql);
			while(rs.next()){
				System.out.println("user_id="+rs.getLong("user_id")+",order_id="+rs.getLong("order_id")+",item_id="+rs.getLong("item_id"));
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testERWithEqualCondition() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		String sql= "select o.user_id, o.order_id,i.item_id from t_order o, t_item i where o.order_id=i.order_id and o.order_id = 38";
		Statement st= conn.createStatement();
		try{
			ResultSet rs= st.executeQuery(sql);
			while(rs.next()){
				System.out.println("user_id="+rs.getLong("user_id")+",order_id="+rs.getLong("order_id")+",item_id="+rs.getLong("item_id"));
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test //TODO 如果第一个结果集没数据则跳过
	public void testERWithInCondition() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		String sql= "select o.user_id, o.order_id,i.item_id from t_order o, t_item i where o.order_id=i.order_id and o.order_id in (38,29,40)";
		Statement st= conn.createStatement();
		try{
			ResultSet rs= st.executeQuery(sql);
			while(rs.next()){
				System.out.println("user_id="+rs.getLong("user_id")+",order_id="+rs.getLong("order_id")+",item_id="+rs.getLong("item_id"));
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testERWithBetweenCondition() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		String sql= "select o.user_id, o.order_id,i.item_id from t_order o, t_item i where o.order_id=i.order_id and o.order_id between 20 and 30";
		Statement st= conn.createStatement();
		try{
			ResultSet rs= st.executeQuery(sql);
			while(rs.next()){
				System.out.println("user_id="+rs.getLong("user_id")+",order_id="+rs.getLong("order_id")+",item_id="+rs.getLong("item_id"));
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testQuerySubtableCondition() throws Exception{
		HintContextHolder.setHintContext(new HintContext(){
			@Override
			public String getPartitionDBName() {
				return "oralcePartition0";
			}
		});
		Connection conn= shardingDataSource.getConnection();
		String sql= "select i.order_id,i.item_id from t_item i where i.order_id = 28 or i.order_id= 35";
		Statement st= conn.createStatement();
		try{
			ResultSet rs= st.executeQuery(sql);
			while(rs.next()){
				System.out.println("order_id="+rs.getLong("order_id")+",item_id="+rs.getLong("item_id"));
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		st.close();
		conn.close();
		HintContextHolder.clear();
	}
	
	@Test
	public void testRoutDBAndTable() throws Exception{
		Connection conn= shardingDataSource.getConnection();
		String sql= "select i.order_id,i.item_id from t_item i where i.order_id = 28 and i.user_id=3";
		Statement st= conn.createStatement();
		try{
			ResultSet rs= st.executeQuery(sql);
			while(rs.next()){
				System.out.println("order_id="+rs.getLong("order_id")+",item_id="+rs.getLong("item_id"));
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		st.close();
		conn.close();
	}
	
	//@Test
	public void testDML() throws Exception{
		Connection conn= shardingDataSource.getConnection();
		String sql= "insert into t_order(order_id,user_id,status) values("+sequence.nextval("t_order")+","+3+",'"+new Date().getTime()+"')";
		conn.setAutoCommit(false);
		Statement st= conn.createStatement();
		try{
			int result= st.executeUpdate(sql);
			System.out.println("result="+result);
			conn.commit();
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
		}
		st.close();
		conn.close();
	}
	
}
