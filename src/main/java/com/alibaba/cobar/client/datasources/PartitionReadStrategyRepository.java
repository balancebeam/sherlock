package com.alibaba.cobar.client.datasources;

import java.util.HashMap;
import java.util.Map;

public class PartitionReadStrategyRepository {

	private Map<String,IPartitionReadStrategy> readStrategies= new HashMap<String,IPartitionReadStrategy>();
	
	public void setReadStrategies(Map<String,IPartitionReadStrategy> readStrategies){
		this.readStrategies= readStrategies;
	}
	
	public IPartitionReadStrategy getReadStrategy(String name){
		return readStrategies.get(name);
	}
}
