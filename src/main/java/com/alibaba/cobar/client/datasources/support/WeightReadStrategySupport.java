package com.alibaba.cobar.client.datasources.support;

import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import com.alibaba.cobar.client.datasources.CobarDataSourceProxy;
import com.alibaba.cobar.client.datasources.IPartitionReadStrategy;
import com.alibaba.cobar.client.datasources.PartitionDataSource;

public class WeightReadStrategySupport implements IPartitionReadStrategy{
	
	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return getDataSourceByWeight(ds,0);
	}
	
	protected DataSource getDataSourceByWeight(PartitionDataSource ds,int w){
		List<DataSource> readDataSources= ds.getReadDataSources();
		if(CollectionUtils.isEmpty(readDataSources)){
			return ds.getWriteDataSource();
		}
    	int total= 0;
		for(DataSource ds0: readDataSources){
			total+= ((CobarDataSourceProxy)ds0).getWeight();
		}
		Random rand = new Random();
		int weight= 0,rdm= rand.nextInt(total+ w);
		if(rdm< total){
			for(DataSource ds0: readDataSources){
    			weight+= ((CobarDataSourceProxy)ds0).getWeight();
    			if(weight> rdm){
    				return ds0;
    			}
    		}
		}
		return ds.getWriteDataSource();
    }

}
