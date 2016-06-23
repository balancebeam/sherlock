package io.pddl.executor.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.executor.ExecuteContext;
import io.pddl.jdbc.ShardingConnection;
import io.pddl.router.table.GlobalTableRepository;
import io.pddl.router.table.LogicTableRepository;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.bean.SQLStatementType;

public class ExecuteContextSupport implements ExecuteContext{

	private Map<String,Connection> connectionMapping;
	
	private SQLStatementType statementType;
	
	private SQLParsedResult sqlParsedResult;
	
	private String logicSql;
	
	private List<Object> parameters;
	
	private ShardingConnection shardingConnection;
	
	private ShardingDataSourceRepository shardingDataSourceRepository;
	
	private GlobalTableRepository globalTableRepository;
	
	private LogicTableRepository logicTableRepository;
	
	public ExecuteContextSupport(
			ShardingConnection shardingConnection,
			ShardingDataSourceRepository shardingDataSourceRepository,
			GlobalTableRepository globalTableRepository,
			LogicTableRepository logicTableRepository){
		this.shardingConnection= shardingConnection;
		this.shardingDataSourceRepository= shardingDataSourceRepository;
		this.globalTableRepository= globalTableRepository;
		this.logicTableRepository= logicTableRepository;
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

	public void setTranOrUpdateConnection(String dataSourceName,Connection conn){
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
	
	public void setParameters(List<Object> parameters){
		this.parameters= parameters;
	}

	@Override
	public List<Object> getParameters() {
		return parameters;
	}

	@Override
	public ShardingDataSourceRepository getShardingDataSourceRepository() {
		return shardingDataSourceRepository;
	}

	@Override
	public GlobalTableRepository getGlobalTableRepository() {
		return globalTableRepository;
	}

	@Override
	public LogicTableRepository getLogicTableRepository() {
		return logicTableRepository;
	}
}
