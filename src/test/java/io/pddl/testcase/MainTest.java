package io.pddl.testcase;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.pddl.testcase.entity.Item;
import io.pddl.testcase.entity.ItemCondition;
import io.pddl.testcase.entity.OrderCondition;
import io.pddl.testcase.service.OrderService;

public class MainTest {

	@SuppressWarnings("resource")
	public static void main(String[] args){
		final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		
		OrderService service= context.getBean(OrderService.class);
		
		long userId= 0;
		List<Map<String,Long>> list= service.addOrders(userId);
		System.out.println(list);
		
		System.out.println("--------------------------------");
		
		OrderCondition condition= new OrderCondition();
		condition.setUserId(userId);
		long[] orderIds= new long[list.size()];
		int index=0;
		for(Map<String,Long> hash: list){
			orderIds[index++]= hash.get("orderId");
		}
		condition.setOrderIds(orderIds);
		List<Object> result= service.queryOrders(condition);
		System.out.println(result);
		
		System.out.println("--------------------------------");
		
		for(Map<String,Long> hash: list){
			Item item= new Item();
			item.setItemId(hash.get("itemId"));
			item.setOrderId(hash.get("orderId"));
			item.setUserId(hash.get("userId"));
			item.setStatus("modify");
			service.updateItem(item);
		}
		
		System.out.println("--------------------------------");
		
		for(Map<String,Long> hash: list){
			ItemCondition con= new ItemCondition();
			con.setUserId(userId);
			con.setOrderId(hash.get("orderId"));
			con.setItemIds(new long[]{hash.get("itemId")});
			service.deleteItems(con);
		}
	}
}
