package io.pddl.testcase.service.impl;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.pddl.testcase.dao.OrderDAO;
import io.pddl.testcase.entity.Order;
import io.pddl.testcase.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService{

	@Resource
	private OrderDAO dao;
	
	@Override
	public void addOrder(Order order) {
		try {
			dao.addOrder(order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
