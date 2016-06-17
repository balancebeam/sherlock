package io.pddl.datasource.support.strategy;

import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import io.pddl.datasource.support.WeightDataSourceProxy;
import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DataSourceReadStrategy;
import io.pddl.datasource.support.PartitionDataSourceSupport;

public class WeightReadStrategySupport implements DataSourceReadStrategy{
	
	private Log logger = LogFactory.getLog(getClass());

	private int totalWeight = 0;
	
	@Override
	public DataSource getSlaveDataSource(PartitionDataSource pds) {
		return getDataSourceByWeight(pds,0);
	}
	
	protected DataSource getDataSourceByWeight(PartitionDataSource pds,int w){
		List<DataSource> slaveDataSources= ((PartitionDataSourceSupport)pds).getSlaveDataSources();
		if(CollectionUtils.isEmpty(slaveDataSources)){
			if(logger.isInfoEnabled()){
				logger.info("SlaveDataSource array of PartitionDataSource ["+pds.getName()+"] is empty, will use MasterDataSource");
			}
			return pds.getMasterDataSource();
		}

		if (totalWeight == 0) {
			// cache total weight
			calculateTotalWeight(slaveDataSources);
		}

		Random rand = new Random();
		int weight= 0,rdm= rand.nextInt(totalWeight + w);
		for(DataSource ds: slaveDataSources){
			weight+= ((WeightDataSourceProxy)ds).getWeight();
			if(weight> rdm){
				if(logger.isInfoEnabled()){
					logger.info("found SlaveDataSource of PartitionDataSource ["+pds.getName()+"], weight: "+((WeightDataSourceProxy)ds).getWeight());
				}
				return ds;
			}
		}
		return pds.getMasterDataSource();
    }
	
	@Override
	public String getStrategyName(){
		return "weight";
	}

	private synchronized void calculateTotalWeight(List<DataSource> lds) {
		if (totalWeight != 0) return;

		int total = 0;
		for(DataSource ds: lds){
			total+= ((WeightDataSourceProxy)ds).getWeight();
		}
		totalWeight = total;
	}

}
