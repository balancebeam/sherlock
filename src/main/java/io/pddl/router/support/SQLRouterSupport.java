package io.pddl.router.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.exception.SQLParserException;
import io.pddl.exception.ShardingDatabaseException;
import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteHolder;
import io.pddl.executor.support.ExecuteContextSupport;
import io.pddl.router.SQLRouter;
import io.pddl.router.database.DatabaseRouter;
import io.pddl.router.table.GlobalTableRepository;
import io.pddl.router.table.LogicTableRouter;
import io.pddl.sqlparser.SQLParseEngine;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.SQLParserFactory;

public class SQLRouterSupport implements SQLRouter{
	
	private DatabaseRouter databaseRouter;
	
	private LogicTableRouter tableRouter;
	
	private GlobalTableRepository globalTableRepository;
	
	private ShardingDataSourceRepository shardingDataSourceRepository;
	
	public void setTableRouter(LogicTableRouter tableRouter){
		this.tableRouter= tableRouter;
	}
	
	public void setDatabaseRouter(DatabaseRouter databaseRouter){
		this.databaseRouter= databaseRouter;
	}
	
	public void setGlobalTableRepository(GlobalTableRepository globalTableRepository){
		this.globalTableRepository= globalTableRepository;
	}
	
	public void setShardingDataSourceRepository(ShardingDataSourceRepository shardingDataSourceRepository){
		this.shardingDataSourceRepository= shardingDataSourceRepository;
	}
	
	@Override
	public List<SQLExecutionUnit> doRoute(ExecuteContext ctx,String sql, List<Object> parameters) throws SQLParserException {
		try{
			//parse the sql
			((ExecuteContextSupport)ctx).setLogicSql(sql);
			SQLParseEngine sqlParseEngine= SQLParserFactory.create(sql, parameters);
			((ExecuteContextSupport)ctx).setStatementType(sqlParseEngine.getStatementType());
			SQLParsedResult sqlParsedResult = sqlParseEngine.parse();
			((ExecuteContextSupport)ctx).setSQLParsedResult(sqlParsedResult);
			
			//first to shard global table
			if(ctx.isDMLOperation()){
				String tableName= ctx.getSQLParsedResult().getFirstTable().getName();
				if(globalTableRepository.isGlobalTable(tableName)){
					List<SQLExecutionUnit> result= new ArrayList<SQLExecutionUnit>();
					for(String dataSourceName: shardingDataSourceRepository.getPartitionDataSourceNames()){
						result.add(new SQLExecutionUnit(dataSourceName,sql));
					}
					return result;
				}
			}
			
			//then to shard database
			Collection<String> dataSourceNames= databaseRouter.doRoute(ctx);
			if(CollectionUtils.isEmpty(dataSourceNames)){
				throw new ShardingDatabaseException("datasource is empty :"+sql);
			}
			
			List<SQLExecutionUnit> result= new ArrayList<SQLExecutionUnit>();
			for(String dataSourceName: dataSourceNames){
				//final to shard table
				Collection<String> sqls= tableRouter.doRoute(ctx,dataSourceName);
				for(String each: sqls){
					result.add(new SQLExecutionUnit(dataSourceName,each));
				}
			}
			return result;
		}finally{
			ExecuteHolder.clear();
		}
	}
}
