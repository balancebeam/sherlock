package io.pddl.testcase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.pddl.testcase.entity.ItemCondition;
import io.pddl.testcase.service.OrderService;

public class MainTest {

	@SuppressWarnings("resource")
	public static void main(String[] args){
		final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		
		OrderService service= context.getBean(OrderService.class);
		
//		Random random= new Random();
//		long userId= random.nextLong();
//		service.addRandomOrder(userId);
		
		System.out.println();
//		Item item= new Item();
//		item.setItemId(4L);
//		item.setOrderId(6L);
//		item.setUserId(0L);
//		item.setStatus("modify");
//		service.updateItem(item);
		
		ItemCondition condition= new ItemCondition();
		condition.setUserId(0L);
		condition.setOrderId(4);
		condition.setItemIds(new long[]{4,6,8});
		service.deleteItems(condition);
	}
}
