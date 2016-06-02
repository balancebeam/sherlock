package io.pddl.testcase.service;

import java.util.List;

import io.pddl.testcase.entity.Item;
import io.pddl.testcase.entity.ItemCondition;
import io.pddl.testcase.entity.Order;

public interface OrderService {

	void addRandomOrder(long userId);
	
	void updateItem(Item item);
	
	void deleteItems(ItemCondition condition);
	
	List<Object> queryOrder(Order order);
}
