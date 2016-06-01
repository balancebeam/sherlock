package com.hj.cobar.testcase;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.pddl.sequence.SequenceGenerator;
import junit.framework.TestCase;

public class ConcurrentSequenceTest extends TestCase{
	
	public void testDbSequence(){
		
		final int nodeNumber= 3;
		final int concurrencyNumber= 5;
		final int executeNumber= 1000;
		final String tabName= "user";
		final CountDownLatch nodeLatch= new CountDownLatch(nodeNumber);
		
		for(int i=0;i<nodeNumber;i++){
			Thread t= new Thread(new Runnable(){
				@Override
				public void run() {
					final CountDownLatch concurrencyLatch= new CountDownLatch(concurrencyNumber);
					ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
					final SequenceGenerator sequence= (SequenceGenerator)context.getBean("sequence");
					for(int j=0;j<concurrencyNumber;j++){
						new Thread(new Runnable(){

							@Override
							public void run() {
								for(int k=0;k<executeNumber;k++){
									sequence.nextval(tabName);
								}
								concurrencyLatch.countDown();
							}
							
						}).start();
						
					}
					try {
						concurrencyLatch.await();
					} catch (InterruptedException e) {
					}
					nodeLatch.countDown();
				}
				
			},("node-"+i));
			t.start();
		}
		
		try {
			nodeLatch.await();
		} catch (InterruptedException e) {
		}
    }
	
	public void testLocalSequence(){
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		SequenceGenerator sequence= (SequenceGenerator)context.getBean("testSequence");
		for(int i=0;i<10;i++){
			System.out.print(sequence.nextval("user")+",");
		}
		System.out.println();
	}

}
