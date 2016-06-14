package io.pddl.router.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.exception.SQLParserException;
import io.pddl.exception.ShardingDataSourceException;
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
	
	private Log logger = LogFactory.getLog(SQLRouterSupport.class);
	
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
	public List<SQLExecutionUnit> doRoute(ExecuteContext ctx,String logicSql, List<Object> parameters) throws SQLParserException {
		try{
			//把逻辑SQL绑定到上下文中
			((ExecuteContextSupport)ctx).setLogicSql(logicSql);
			//把Prepared的值绑定到上下文中
			((ExecuteContextSupport)ctx).setParameters(parameters);
			//创建SQL解析引擎
			SQLParseEngine sqlParseEngine= SQLParserFactory.create(logicSql, parameters);
			//把SQL操作类型绑定到上下文中
			((ExecuteContextSupport)ctx).setStatementType(sqlParseEngine.getStatementType());
			logger.info("SQLStatementType: "+sqlParseEngine.getStatementType());
			//解析SQL语句，包括所有的表Table、字段Condition和实际SQL构建器
			SQLParsedResult sqlParsedResult = sqlParseEngine.parse();
			//把解析结果绑定到上下文中
			((ExecuteContextSupport)ctx).setSQLParsedResult(sqlParsedResult);
			logger.info("SQLParsedResult: "+sqlParsedResult);
			
			//如果是写操作[INSERT | UPDATE | DELETE]
			if(ctx.isDMLOperation()){
				String tableName= ctx.getSQLParsedResult().getFirstTable().getName();
				//如果是字典表或全局表，则返回所有数据源和logicSql的执行单元
				if(globalTableRepository.isGlobalTable(tableName)){
					logger.info(tableName+" is global table");
					List<SQLExecutionUnit> result= new ArrayList<SQLExecutionUnit>();
					for(String dataSourceName: shardingDataSourceRepository.getPartitionDataSourceNames()){
						result.add(new SQLExecutionUnit(dataSourceName,logicSql));
					}
					logger.info("Sharding global table result: " + result.toString());
					return result;
				}
			}
			
			//然后执行数据库路由
			Collection<String> databaseNames= databaseRouter.doRoute(ctx);
			if(CollectionUtils.isEmpty(databaseNames)){
				throw new ShardingDataSourceException("dataSource is empty :"+logicSql);
			}
			logger.info("Sharding database Names: " + databaseNames);
			
			List<SQLExecutionUnit> result= new ArrayList<SQLExecutionUnit>();
			for(String databaseName: databaseNames){
				//最后执行表路由
				Collection<String> sqls= tableRouter.doRoute(ctx,databaseName);
				logger.info("Sharding table sqls: " + sqls);
				for(String each: sqls){
					result.add(new SQLExecutionUnit(databaseName,each));
				}
			}
			logger.info("Sharding final result: " + result);
			return result;
		}finally{
			ExecuteHolder.clear();
		}
	}
}
