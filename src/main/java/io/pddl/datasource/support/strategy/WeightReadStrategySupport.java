package io.pddl.datasource.support.strategy;

import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import io.pddl.datasource.support.DefaultDataSourceProxy;
import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DatabaseReadStrategy;
import io.pddl.datasource.support.PartitionDataSourceSupport;

public class WeightReadStrategySupport implements DatabaseReadStrategy{
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource ds) {
		return getDataSourceByWeight(ds,0);
	}
	
	protected DataSource getDataSourceByWeight(PartitionDataSource ds,int w){
		List<DataSource> readDataSources= ((PartitionDataSourceSupport)ds).getReadDataSources();
		if(CollectionUtils.isEmpty(readDataSources)){
			return ds.getMasterDataSource();
		}
    	int total= 0;
		for(DataSource ds0: readDataSources){
			total+= ((DefaultDataSourceProxy)ds0).getWeight();
		}
		Random rand = new Random();
		int weight= 0,rdm= rand.nextInt(total+ w);
		if(rdm< total){
			for(DataSource ds0: readDataSources){
    			weight+= ((DefaultDataSourceProxy)ds0).getWeight();
    			if(weight> rdm){
    				return ds0;
    			}
    		}
		}
		return ds.getMasterDataSource();
    }
	
	@Override
	public String getStrategyName(){
		return "weight";
	}

}
