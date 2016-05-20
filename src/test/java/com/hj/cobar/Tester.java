package com.hj.cobar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.cobar.client.sequence.SequenceGenerator;
import com.hj.cobar.bean.Cont;
import com.hj.cobar.query.ContQuery;
import com.hj.cobar.service.ContService;

public class Tester {
	
	private void test_sequence(){
		final int count= 20;
		final CountDownLatch latch= new CountDownLatch(count);
		final CountDownLatch latch2 = new CountDownLatch(1);
//		final CyclicBarrier barrier= new CyclicBarrier(count);
		final CountDownLatch latch3= new CountDownLatch(count);
		long begin= System.currentTimeMillis();
		for(int i=0;i<count;i++){
			new Thread(new Runnable(){

				@Override
				public void run() {
					latch.countDown();
					
					final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
					final SequenceGenerator sequence= (SequenceGenerator)context.getBean("sequence");
					try {
						latch2.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long b= System.currentTimeMillis();
					long val=0;
					StringBuffer buf= new StringBuffer();
					for(int j=0;j<500;j++){
						val= sequence.nextval("hello");
						//buf.append(","+val);
						//System.out.print(","+val);
					}
					long e= System.currentTimeMillis();
					//System.out.println(val+" ,"+(e-b));
					//System.out.println(Thread.currentThread().getName()+buf.toString());
//					try {
//						barrier.await();
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (BrokenBarrierException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					long end= System.currentTimeMillis();
					latch3.countDown();
				}
				
			},"["+(i+1)+"]").start();
			
		}
		
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		latch2.countDown();
		long end= System.currentTimeMillis();
		System.out.println("every thread is prepared ,"+(end-begin));
		long begin2= System.currentTimeMillis();
		try {
			latch3.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end2= System.currentTimeMillis();
		System.out.println("thread concurrent execute ,"+(end2-begin2));
	}

	public static void main(String[] args){
		
//		ContService contService = (ContService) context.getBean("contService");
//		
//    	ContQuery contQuery = new ContQuery();
//    	contQuery.setTaobaoId(0L);
//    	List<Cont> list = contService.getContList(contQuery);
//    	if(list != null && !list.isEmpty()){
//    		for(int i=0;i<list.size();i++)
//    		System.out.println(list.get(i));
//    	}
		
//    	Cont cont = new Cont();
//    	cont.setName("gd");
//    	Long taobaoId = new Long(new Random().nextInt(10000));
//    	System.out.println("#"+taobaoId);
//    	cont.setTaobaoId(taobaoId);
//    	cont.setName("xxx");
//    	cont.setId(17L);
//    	contService.addCont(cont);
		
//		Cont cont = contService.getContByKey(2L);
//    	System.out.println(cont);
		
		final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		final SequenceGenerator sequence= (SequenceGenerator)context.getBean("testSequence");
		ContService service= context.getBean(ContService.class);
//		for(int i=0;i<10;i++){
//			
//			long val= sequence.nextval("hello");
//			System.out.println(val);
//		}
//		
//		InputStream in= System.in;
		ContService contService = (ContService) context.getBean("contService");
		Cont cont = new Cont();
    	cont.setId(sequence.nextval("cont"));
    	cont.setName("gd");
    	Long taobaoId = new Long(new Random().nextInt(10000));
    	System.out.println("#"+taobaoId);
    	cont.setTaobaoId(taobaoId);
    	System.out.println(contService.addCont(cont));
//		
//    	ContQuery contQuery = new ContQuery();
//    	contQuery.setTaobaoId(963L);
//    	List<Cont> list = contService.getContList(contQuery);
//    	if(list != null){
//    		System.out.println(list);
//    	}
//		Cont c1= new Cont();
//		c1.setId(sequence.nextval("cont"));
//		c1.setTaobaoId(new Long(new Random().nextInt(10000)));
//		c1.setName("xxx");
//		Cont c2= new Cont();
//		c2.setId(sequence.nextval("cont"));
//		c2.setTaobaoId(new Long(new Random().nextInt(10000)));
//		c2.setName("yyy");
//		Cont c3= new Cont();
//		c3.setId(sequence.nextval("cont"));
//		c3.setTaobaoId(new Long(new Random().nextInt(10000)));
//		c3.setName("zzz");
//		List<Cont> list= new ArrayList<>();
//		list.add(c1);
//		list.add(c2);
//		list.add(c3);
//		service.addBatchCont(list);
//		ContQuery contQuery = new ContQuery();
//    	contQuery.setTaobaoId(0L);
//    	List<Cont> list = service.getContList(contQuery);
//    	if(list != null){
//    		System.out.println(list);
//    	}
	}
}
