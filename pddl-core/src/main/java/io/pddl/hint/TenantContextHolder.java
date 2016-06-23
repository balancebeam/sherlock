package io.pddl.hint;

final public class TenantContextHolder {
	
	private TenantContextHolder(){}
	
	private static ThreadLocal<TenantContext> holder= new ThreadLocal<TenantContext>();
	
	public static TenantContext getTenantContext(){
		return holder.get();
	}
	
	public static void setTenantContext(TenantContext tenantContext){
		holder.set(tenantContext);
	}
	
	public static void clear(){
		holder.set(null);
	}
}
