package io.pddl.datasource.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.pddl.datasource.DataSourceReadStrategy;
import io.pddl.datasource.support.strategy.OnlyWriteReadStrategySupport;
import io.pddl.datasource.support.strategy.CycleReadStrategySupport;
import io.pddl.datasource.support.strategy.CycleReadStrategyWithWriteSupport;
import io.pddl.datasource.support.strategy.WeightReadStrategySupport;
import io.pddl.datasource.support.strategy.WeightReadStrategyWithWriteSupport;

final public class DatabaseReadStrategyRepository {
	
	private DatabaseReadStrategyRepository(){}

	private static Map<String,DataSourceReadStrategy> dataSourceReadStrategies= new HashMap<String,DataSourceReadStrategy>();
	
	static{
		Class<?>[] strategyClasses= new Class<?>[]{
			OnlyWriteReadStrategySupport.class,
			CycleReadStrategySupport.class,
			CycleReadStrategyWithWriteSupport.class,
			WeightReadStrategySupport.class,
			WeightReadStrategyWithWriteSupport.class
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
