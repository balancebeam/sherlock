package io.pddl.executor;

public abstract class ExecutorContextHolder {

	private static ThreadLocal<ExecutorContext> holder= new ThreadLocal<ExecutorContext>();
	
	public static ExecutorContext getContext(){
		return holder.get();
	}
	
	public static void setContext(ExecutorContext executorContext){
		holder.set(executorContext);
	}
	
}
