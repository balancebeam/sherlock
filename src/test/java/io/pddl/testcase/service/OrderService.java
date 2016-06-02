package io.pddl.testcase.service;

import java.util.List;
import java.util.Map;

import io.pddl.testcase.entity.Item;
import io.pddl.testcase.entity.ItemCondition;
import io.pddl.testcase.entity.OrderCondition;

public interface OrderService {

	List<Map<String,Long>> addOrders(long userId);
	
	void updateItem(Item item);
	
	void deleteItems(ItemCondition condition);
	
	List<Object> queryOrders(OrderCondition condition);
}
