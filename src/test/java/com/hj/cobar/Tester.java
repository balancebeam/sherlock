package com.hj.cobar;

import java.util.List;
import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hj.cobar.bean.Cont;
import com.hj.cobar.query.ContQuery;
import com.hj.cobar.service.ContService;

public class Tester {

	public static void main(String[] args){
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		ContService contService = (ContService) context.getBean("contService");
		
    	ContQuery contQuery = new ContQuery();
    	contQuery.setTaobaoId(0L);
    	List<Cont> list = contService.getContList(contQuery);
    	if(list != null && !list.isEmpty()){
    		for(int i=0;i<list.size();i++)
    		System.out.println(list.get(i));
    	}
		
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

	}
}
