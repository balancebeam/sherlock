package com.hj.cobar.testcase;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.cobar.client.sequence.SequenceGenerator;

public class ZooKeeperSequence {

	public static void main(String[] args) {


		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		final SequenceGenerator sequence= (SequenceGenerator)context.getBean("mysqlsequence");

		long id = sequence.nextval("mysqlsequence");
		
		for(int i = 0;i<100;i++){
			System.out.println(sequence.nextval("mysqlsequence"));
		}
		
		System.out.println(id);
		
		System.out.println("");
		
		
		final SequenceGenerator zksequence= (SequenceGenerator)context.getBean("zksequence");
		
		id = zksequence.nextval("zksequence");
		
		for(int i = 0;i<100;i++){
			System.out.println(sequence.nextval("zksequence"));
		}
		
		
		Builder builder = CuratorFrameworkFactory.builder().connectString("localhost:2181")
				.retryPolicy(new ExponentialBackoffRetry(1000, 3, 3000)).namespace("cobarclientx");
		final CuratorFramework client = builder.build();
		client.start();
		try {
			client.blockUntilConnected();
		} catch (Exception e) {
		}
		
		final String name = "name";
		new Thread(){
			public void run(){
				InterProcessMutex lock = new InterProcessMutex(client, "/lock/" + name);
				
				try {
					lock.acquire();
					System.out.println("fdsafasdfa");
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						lock.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
			}
		}.start();
			
		new Thread(){
			public void run(){
				InterProcessMutex lock = new InterProcessMutex(client, "/lock/" + name);
				
				try {
					lock.acquire();
					System.out.println("Fsafsdafds");
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						lock.release();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		}.start();
		
		
		Builder builder1 = CuratorFrameworkFactory.builder().connectString("localhost:2181")
				.retryPolicy(new ExponentialBackoffRetry(1000, 3, 3000)).namespace("cobarclientx");
		final CuratorFramework client1 = builder.build();
		client1.start();
		try {
			client1.blockUntilConnected();
		} catch (Exception e) {
		}
		
		new Thread(){
			public void run(){
				InterProcessMutex lock = new InterProcessMutex(client1, "/lock/" + name);
				
				try {
					lock.acquire();
					System.out.println("Fsafsdafds");
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						lock.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
			}
		}.start();
		
		
		
	}

}
