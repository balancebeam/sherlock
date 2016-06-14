package io.pddl.datasource.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.pddl.datasource.DatabaseReadStrategy;
import io.pddl.datasource.support.strategy.OnlyWriteReadStrategySupport;
import io.pddl.datasource.support.strategy.PollingReadStrategySupport;
import io.pddl.datasource.support.strategy.PollingReadStrategyWithWriteSupport;
import io.pddl.datasource.support.strategy.WeightReadStrategySupport;
import io.pddl.datasource.support.strategy.WeightReadStrategyWithWriteSupport;

final public class DatabaseReadStrategyRepository {
	
	private DatabaseReadStrategyRepository(){}

	private static Map<String,DatabaseReadStrategy> databaseReadStrategies= new HashMap<String,DatabaseReadStrategy>();
	
	static{
		Class<?>[] strategyClasses= new Class<?>[]{
			OnlyWriteReadStrategySupport.class,
			PollingReadStrategySupport.class,
			PollingReadStrategyWithWriteSupport.class,
			WeightReadStrategySupport.class,
			WeightReadStrategyWithWriteSupport.class
		};
		Log logger = LogFactory.getLog(DatabaseReadStrategyRepository.class);
			for(Class<?> cls: strategyClasses){
				try {
					DatabaseReadStrategy instance= (DatabaseReadStrategy)cls.newInstance();
					databaseReadStrategies.put(instance.getStrategyName(), instance);
					if(logger.isInfoEnabled()){
						logger.info("init read strategy ["+instance.getStrategyName()+"] "+instance);
					}
				} catch (Exception e) {
					logger.error("init read strategy error",e);
				}
			}
	}

	
	public static DatabaseReadStrategy getDatabaseReadStrategy(String name){
		return databaseReadStrategies.get(name);
	}
}
