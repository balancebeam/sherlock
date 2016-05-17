package com.alibaba.cobar.client.executor;

public abstract class ExecutorContextHolder {

	private static ThreadLocal<IExecutorContext> holder= new ThreadLocal<IExecutorContext>();
	
	public static IExecutorContext getExecutorContext(){
		return holder.get();
	}
	
	public static void setExecutorContext(IExecutorContext executorContext){
		holder.set(executorContext);
	}
	
}
