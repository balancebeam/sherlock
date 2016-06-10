package io.pddl.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import io.pddl.datasource.DatabaseType;
import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.executor.ExecuteProcessor;
import io.pddl.jdbc.adapter.AbstractDataSourceAdapter;
import io.pddl.router.SQLRouter;

public class ShardingDataSource extends AbstractDataSourceAdapter{
    
    private SQLRouter sqlRouter;
    
    private ExecuteProcessor processor;
    
    private ShardingDataSourceRepository shardingDataSourceRepository;
    
    public void setSqlRouter(SQLRouter sqlRouter){
    	this.sqlRouter= sqlRouter;
    }
    
    public void setProcessor(ExecuteProcessor processor){
    	this.processor= processor;
    }
    
    public void setShardingDataSourceRepository(ShardingDataSourceRepository shardingDataSourceRepository){
    	this.shardingDataSourceRepository= shardingDataSourceRepository;
    }
    
    public void setDatabaseType(DatabaseType databaseType){
    	DatabaseType.setApplicationDatabaseType(databaseType);
    }
    
	@Override
	public Connection getConnection() throws SQLException {
		return new ShardingConnection(shardingDataSourceRepository,sqlRouter,processor);
	}
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection();
	}
}
