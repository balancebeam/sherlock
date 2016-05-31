package com.alibaba.cobar.client.executor;

public abstract class ExecutorContextHolder {

	private static ThreadLocal<IExecutorContext> holder= new ThreadLocal<IExecutorContext>();
	
	public static IExecutorContext getContext(){
		return holder.get();
	}
	
	public static void setContext(IExecutorContext executorContext){
		holder.set(executorContext);
	}
	
}
