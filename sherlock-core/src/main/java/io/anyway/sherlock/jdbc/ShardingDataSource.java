package io.anyway.sherlock.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import io.anyway.sherlock.executor.ExecuteStatementProcessor;
import io.anyway.sherlock.router.table.GlobalTableRepository;
import io.anyway.sherlock.datasource.ShardingDataSourceRepository;
import io.anyway.sherlock.jdbc.adapter.AbstractDataSourceAdapter;
import io.anyway.sherlock.router.SQLRouter;
import io.anyway.sherlock.router.table.LogicTableRepository;

/**
 * 分片数据源
 * @author yangzz
 *
 */
public class ShardingDataSource extends AbstractDataSourceAdapter{
    //SQL数据源和表路由器
    SQLRouter sqlRouter;
    
    ExecuteStatementProcessor processor;
    
    ShardingDataSourceRepository shardingDataSourceRepository;
    
    GlobalTableRepository globalTableRepository;
    
    LogicTableRepository logicTableRepository;
    
    public void setSqlRouter(SQLRouter sqlRouter){
    	this.sqlRouter= sqlRouter;
    }
    
    public void setProcessor(ExecuteStatementProcessor processor){
    	this.processor= processor;
    }
    
    public void setShardingDataSourceRepository(ShardingDataSourceRepository shardingDataSourceRepository){
    	this.shardingDataSourceRepository= shardingDataSourceRepository;
    }
    
    public void setGlobalTableRepository(GlobalTableRepository globalTableRepository){
    	this.globalTableRepository= globalTableRepository;
    }
    
    public void setLogicTableRepository(LogicTableRepository logicTableRepository){
    	this.logicTableRepository= logicTableRepository;
    }
    
	@Override
	public Connection getConnection() throws SQLException {
		return new ShardingConnection(this);
	}
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection();
	}
}
