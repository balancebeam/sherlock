package com.hj.cobar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.cobar.client.sequence.SequenceGenerator;
import com.hj.cobar.bean.Cont;
import com.hj.cobar.query.ContQuery;
import com.hj.cobar.service.ContService;

import junit.framework.TestCase;

/**
 * 
 */
public class AppTest extends TestCase{
	ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
	ContService contService = (ContService) context.getBean("contService");
	SequenceGenerator sequence= (SequenceGenerator)context.getBean("testSequence");
	
	/**
	 * 没有使用对象查询直接使用基本类型则到默认的数据源中去查找数据
	 */
    public void test1(){
    	Cont cont = contService.getContByKey(2L);
    	System.out.println(cont);
    }
    
    /**
     * 测试添加
     */
    public void test2(){
    	Cont cont = new Cont();
    	cont.setId(sequence.nextval("cont"));
    	cont.setName("gd");
    	Long taobaoId = new Long(new Random().nextInt(10000));
    	System.out.println("#"+taobaoId);
    	cont.setTaobaoId(taobaoId);
    	System.out.println("contId: "+contService.addCont(cont));
    }
    
    /**
     * 测试使用对象包含taobaoId属性的进行查找
     * 使用这种方式可以根据taobaoId分库查找
     */
    public void test3(){
    	ContQuery contQuery = new ContQuery();
    	contQuery.setTaobaoId(0L);
    	List<Cont> list = contService.getContList(contQuery);
    	if(list != null){
    		System.out.println(list);
    	}
    }
    
    public void test4(){
		Cont c1= new Cont();
		c1.setId(sequence.nextval("cont"));
		c1.setTaobaoId(new Long(new Random().nextInt(10000)));
		c1.setName("xxx");
		Cont c2= new Cont();
		c2.setId(sequence.nextval("cont"));
		c2.setTaobaoId(new Long(new Random().nextInt(10000)));
		c2.setName("yyy");
		Cont c3= new Cont();
		c3.setId(sequence.nextval("cont"));
		c3.setTaobaoId(new Long(new Random().nextInt(10000)));
		c3.setName("zzz");
		List<Cont> list= new ArrayList<Cont>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		contService.addBatchCont(list);
    }
}
