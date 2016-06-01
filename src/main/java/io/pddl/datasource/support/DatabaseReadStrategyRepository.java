package io.pddl.datasource.support;

import java.util.HashMap;
import java.util.Map;

import io.pddl.datasource.DatabaseReadStrategy;
import io.pddl.datasource.support.strategy.OnlyWriteReadStrategySupport;
import io.pddl.datasource.support.strategy.PollingReadStrategySupport;
import io.pddl.datasource.support.strategy.PollingReadStrategyWithWriteSupport;
import io.pddl.datasource.support.strategy.PowerReadStrategySupport;
import io.pddl.datasource.support.strategy.PowerReadStrategyWithWriteSupport;
import io.pddl.datasource.support.strategy.WeightReadStrategySupport;
import io.pddl.datasource.support.strategy.WeightReadStrategyWithWriteSupport;

final public class DatabaseReadStrategyRepository {

	private static Map<String,DatabaseReadStrategy> databaseReadStrategies= new HashMap<String,DatabaseReadStrategy>();
	
	static{
		Class<?>[] strategyClasses= new Class<?>[]{
			OnlyWriteReadStrategySupport.class,
			PollingReadStrategySupport.class,
			PollingReadStrategyWithWriteSupport.class,
			PowerReadStrategySupport.class,
			PowerReadStrategyWithWriteSupport.class,
			WeightReadStrategySupport.class,
			WeightReadStrategyWithWriteSupport.class
		};
		try {
			for(Class<?> cls: strategyClasses){
				DatabaseReadStrategy instance= (DatabaseReadStrategy)cls.newInstance();
				databaseReadStrategies.put(instance.getStrategyName(), instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static DatabaseReadStrategy getDatabaseReadStrategy(String name){
		return databaseReadStrategies.get(name);
	}
}
