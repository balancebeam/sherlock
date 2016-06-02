package io.pddl.testcase.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.pddl.sequence.SequenceGenerator;
import io.pddl.testcase.dao.OrderDAO;
import io.pddl.testcase.entity.Item;
import io.pddl.testcase.entity.ItemCondition;
import io.pddl.testcase.entity.ItemExt;
import io.pddl.testcase.entity.Order;
import io.pddl.testcase.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService{

	@Resource
	private OrderDAO dao;
	
	@Autowired
	@Qualifier("testSequence")
	private SequenceGenerator sequence;
	
	@Override
	@Transactional
	public void addRandomOrder(long userId) {
		try {
			Random random= new Random();
			
			long orderId= sequence.nextval("order");
			Order order= new Order();
			order.setUserId(userId);
			order.setOrderId(orderId);
			order.setStatus("status"+random.nextInt());
			dao.addOrder(order);
			
			long itemId= sequence.nextval("item");
			Item item= new Item();
			item.setItemId(itemId);
			item.setOrderId(orderId);
			item.setUserId(userId);
			item.setStatus("status"+random.nextInt());
			dao.addItem(item);
			
			long extId= sequence.nextval("ext");
			ItemExt ext= new ItemExt();
			ext.setExtId(extId);
			ext.setItemId(itemId);
			ext.setUserId(userId);
			ext.setStatus("status"+random.nextInt());
			dao.addItemExt(ext);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public void updateItem(Item item){
		dao.updateItem(item);
	}
	
	@Transactional
	public void deleteItems(ItemCondition condition){
		dao.deleteItemExt(condition);
		dao.deleteItem(condition);
	}
	
	public List<Object> queryOrder(Order order){
		return dao.queryOrder(order);
	}

}
