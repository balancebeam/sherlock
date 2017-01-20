package io.anyway.sherlock.datasource.support;

import java.util.HashMap;
import java.util.Map;

import io.anyway.sherlock.datasource.DataSourceReadStrategy;
import io.anyway.sherlock.datasource.support.strategy.RoundRobinStrategySupport;
import io.anyway.sherlock.datasource.support.strategy.WeightStrategySupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.anyway.sherlock.datasource.support.strategy.MasterStrategySupport;
import io.anyway.sherlock.datasource.support.strategy.RoundRobinStrategyWithMasterSupport;
import io.anyway.sherlock.datasource.support.strategy.WeightStrategyWithMasterSupport;

final public class DatabaseReadStrategyRepository {
	
	private DatabaseReadStrategyRepository(){}

	private static Map<String,DataSourceReadStrategy> dataSourceReadStrategies= new HashMap<String,DataSourceReadStrategy>();
	
	static{
		Class<?>[] strategyClasses= new Class<?>[]{
			MasterStrategySupport.class,
			RoundRobinStrategySupport.class,
			RoundRobinStrategyWithMasterSupport.class,
			WeightStrategySupport.class,
			WeightStrategyWithMasterSupport.class
		};
		Log logger = LogFactory.getLog(DatabaseReadStrategyRepository.class);
			for(Class<?> cls: strategyClasses){
				try {
					DataSourceReadStrategy instance= (DataSourceReadStrategy)cls.newInstance();
					dataSourceReadStrategies.put(instance.getStrategyName(), instance);
					if(logger.isInfoEnabled()){
						logger.info("init read strategy ["+instance.getStrategyName()+"] "+instance);
					}
				} catch (Exception e) {
					logger.error("init read strategy error",e);
				}
			}
	}

	
	public static DataSourceReadStrategy getDatabaseReadStrategy(String name){
		return dataSourceReadStrategies.get(name);
	}
}
