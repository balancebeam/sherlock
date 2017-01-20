package io.anyway.sherlock.hint;

final public class HintContextHolder {
	
	private HintContextHolder(){}
	
	private static ThreadLocal<HintContext> holder= new ThreadLocal<HintContext>();
	
	public static HintContext getHintContext(){
		return holder.get();
	}
	
	public static void setHintContext(HintContext hintContext){
		holder.set(hintContext);
	}
	
	public static void clear(){
		holder.set(null);
	}
}
