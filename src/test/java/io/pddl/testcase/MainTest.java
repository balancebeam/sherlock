package io.pddl.testcase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.pddl.sequence.SequenceGenerator;
import io.pddl.testcase.entity.Order;
import io.pddl.testcase.service.OrderService;

public class MainTest {

	@SuppressWarnings("resource")
	public static void main(String[] args){
		final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		final SequenceGenerator sequence= (SequenceGenerator)context.getBean("testSequence");
		
		OrderService service= context.getBean(OrderService.class);
		
		Order order= new Order(1,sequence.nextval("order"),"new");
		
		service.addOrder(order);
		
		System.out.println();
	}
}
