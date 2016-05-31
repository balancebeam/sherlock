package io.pddl.testcase.dao;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import io.pddl.testcase.entity.Order;

@Repository
public class OrderDAO {

	@Resource
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	public Object addOrder(Order order) throws SQLException {
		return  this.sqlMapClientTemplate.insert("Order.addOrder", order);
	}
}
