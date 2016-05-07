package com.alibaba.cobar.client.executor;

public class ExecutorContextHolder {

	private static ThreadLocal<IExecutorContext> holder= new ThreadLocal<>();
	
	public static IExecutorContext getExecutorContext(){
		return holder.get();
	}
	
	public static void setExecutorContext(IExecutorContext executorContext){
		holder.set(executorContext);
	}
	
}
