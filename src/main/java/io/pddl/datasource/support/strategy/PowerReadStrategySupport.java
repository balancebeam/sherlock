package io.pddl.datasource.support.strategy;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.support.DefaultPartitionDataSource;
import io.pddl.datasource.DatabaseReadStrategy;

public class PowerReadStrategySupport implements DatabaseReadStrategy{
	
	private ConcurrentHashMap<String,AtomicLong> hash= new ConcurrentHashMap<String,AtomicLong>();

	@Override
	public DataSource getReadDataSource(PartitionDataSource ds) {
		return getDataSourceByPower(ds,0);
	}
	
	protected DataSource getDataSourceByPower(PartitionDataSource ds,int w){
		List<DataSource> readDataSources= ((DefaultPartitionDataSource)ds).getReadDataSources();
		if(CollectionUtils.isEmpty(readDataSources)){
			return ds.getWriteDataSource();
		}
		AtomicLong next= hash.get(ds.getName());
		if(next== null){
			hash.putIfAbsent(ds.getName(), new AtomicLong(0));
			if((next= hash.get(ds.getName()))==null){
				return getDataSourceByPower(ds,w);
			}
		}
    	int total= readDataSources.size()+ w -1,
        	idx= (int)(next.getAndIncrement() & total);
        return idx< readDataSources.size()? readDataSources.get(idx): ds.getWriteDataSource();
    }
	
	@Override
	public String getStrategyName(){
		return "power";
	}

}
