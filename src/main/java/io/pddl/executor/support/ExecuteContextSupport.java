package io.pddl.executor.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import io.pddl.executor.ExecuteContext;
import io.pddl.jdbc.ShardingConnection;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.bean.SQLStatementType;

public class ExecuteContextSupport implements ExecuteContext{

	private Map<String,Connection> connectionMapping;
	
	private SQLStatementType statementType;
	
	private SQLParsedResult sqlParsedResult;
	
	private String logicSql;
	
	private List<Object> parameters;
	
	private ShardingConnection shardingConnection;
	
	public ExecuteContextSupport(ShardingConnection shardingConnection){
		this.shardingConnection= shardingConnection;
	}
	
	@Override
	public boolean isSimplyDQLOperation() {
		try {
			return shardingConnection.getAutoCommit() && SQLStatementType.SELECT == statementType;
		} catch (SQLException e) {
			return false;
		}
	}
	
	@Override
	public boolean isDMLOperation(){
		return SQLStatementType.SELECT != statementType;
	}

	public void setSQLParsedResult(SQLParsedResult sqlParsedResult){
		this.sqlParsedResult= sqlParsedResult;
	}

	@Override
	public SQLParsedResult getSQLParsedResult() {
		return sqlParsedResult;
	}

	public synchronized void setTranOrUpdateConnection(String dataSourceName,Connection conn){
		if(connectionMapping== null){
			connectionMapping= new HashMap<String,Connection>();
		}
		connectionMapping.put(dataSourceName, conn);
	}
	
	public Connection getTranOrUpdateConnection(String dataSourceName) {
		if(CollectionUtils.isEmpty(connectionMapping)){
			return null;
		}
		return connectionMapping.get(dataSourceName);
	}
	
	public void setStatementType(SQLStatementType statementType){
		this.statementType= statementType;
	}
	
	public SQLStatementType getStatementType(){
		return statementType;
	}
	
	public void setLogicSql(String logicSql){
		this.logicSql= logicSql;
	}
	
	@Override
	public String getLogicSql(){
		return logicSql;
	}

	@Override
	public ShardingConnection getShardingConnection() {
		return shardingConnection;
	}
	
	@Override
	public Collection<String> getAvailableDataSourceNames(){
		return shardingConnection.getShardingDataSourceRepository().getPartitionDataSourceNames();
	}
	
	public void setParameters(List<Object> parameters){
		this.parameters= parameters;
	}

	@Override
	public List<Object> getParameters() {
		return parameters;
	}
}
