package io.pddl.executor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;

final public class ExecuteHolder {
	
	private ExecuteHolder(){}

	private static ThreadLocal<Map<String,Object>> holder= new ThreadLocal<Map<String,Object>>();
	
	public static Object getAttribute(String key){
		Map<String,Object> hash= holder.get();
		if(CollectionUtils.isEmpty(hash)){
			return null;
		}
		return hash.get(key);
	}
	
	public static void setAttribute(String key,Object value){
		Map<String,Object> hash= holder.get();
		if(hash== null){
			holder.set(hash= new HashMap<String,Object>());
		}
		hash.put(key, value);
	}
	
	public static void clear(){
		holder.remove();
	}
	
}
