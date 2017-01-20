package io.anyway.sherlock.router.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.anyway.sherlock.exception.SQLParserException;
import io.anyway.sherlock.executor.ExecuteContext;
import io.anyway.sherlock.executor.ExecuteHolder;
import io.anyway.sherlock.executor.support.ExecuteContextSupport;
import io.anyway.sherlock.hint.HintContext;
import io.anyway.sherlock.hint.HintContextHolder;
import io.anyway.sherlock.router.SQLRouter;
import io.anyway.sherlock.router.database.DatabaseRouter;
import io.anyway.sherlock.router.table.LogicTableRouter;
import io.anyway.sherlock.sqlparser.SQLParseEngine;
import io.anyway.sherlock.sqlparser.SQLParserFactory;
import io.anyway.sherlock.sqlparser.bean.SQLStatementType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import io.anyway.sherlock.sqlparser.SQLParsedResult;

/**
 * SQL路由实现先进行数据库路由然后进行表路由
 * @author yangzz
 *
 */
public class SQLRouterSupport implements SQLRouter {
	
	private Log logger = LogFactory.getLog(SQLRouterSupport.class);
	
	private DatabaseRouter databaseRouter;
	
	private LogicTableRouter tableRouter;

	final Pattern pattern= Pattern.compile("(?i)[a-z]+");

	public void setTableRouter(LogicTableRouter tableRouter){
		this.tableRouter= tableRouter;
	}
	
	public void setDatabaseRouter(DatabaseRouter databaseRouter){
		this.databaseRouter= databaseRouter;
	}
	
	@Override
	public List<SQLExecutionUnit> doRoute(final ExecuteContext ctx, final String logicSql, final List<Object> parameters) throws SQLParserException {
		try{
			if(logger.isDebugEnabled()){
				logger.debug("begin to route logicSql: "+logicSql);
			}
			//把逻辑SQL绑定到上下文中
			((ExecuteContextSupport)ctx).setLogicSql(logicSql);
			//把Prepared的值绑定到上下文中
			((ExecuteContextSupport)ctx).setParameters(parameters);
			//全局表和逻辑表为空时，可以只作为读写分离操作
			if(ctx.getGlobalTableRepository().isEmpty() && ctx.getLogicTableRepository().isEmpty()){
				SQLStatementType sqlStatementType= parseStatementType4RW(logicSql);
				((ExecuteContextSupport)ctx).setStatementType(sqlStatementType);
				//使用默认的数据源
				return Collections.singletonList(new SQLExecutionUnit(ctx.getShardingDataSourceRepository().getDefaultDataSource().getName(),logicSql));
			}
			//创建SQL解析引擎
			SQLParseEngine sqlParseEngine= SQLParserFactory.create(ctx.getDatabaseType(),logicSql, parameters);
			//把SQL操作类型绑定到上下文中
			((ExecuteContextSupport)ctx).setStatementType(sqlParseEngine.getStatementType());
			if(logger.isDebugEnabled()){
				logger.debug("SQLStatementType: "+sqlParseEngine.getStatementType());
			}
			//解析SQL语句，包括所有的表Table、字段Condition和实际SQL构建器
			SQLParsedResult sqlParsedResult = sqlParseEngine.parse();
			//把解析结果绑定到上下文中
			((ExecuteContextSupport)ctx).setSQLParsedResult(sqlParsedResult);
			if(logger.isInfoEnabled()){
				logger.info("SQLParsedResult: "+sqlParsedResult);
			}
			
			//如果是写操作[INSERT | UPDATE | DELETE]
			if(ctx.isDMLOperation()){
				String tableName= ctx.getSQLParsedResult().getFirstTable().getName();
				//如果是字典表或全局表，则返回所有数据源和logicSql的执行单元
				if(ctx.getGlobalTableRepository().isGlobalTable(tableName)){
					if(logger.isInfoEnabled()){
						logger.info(tableName+" is global table");
					}
					List<SQLExecutionUnit> result= new ArrayList<SQLExecutionUnit>();
					//获取全局表数据分片名称集合
					Collection<String> partitionDataSourceNames= ctx.getGlobalTableRepository().getPartitionDataSourceNames(tableName);
					//如果定义分片集合则使用全集
					if(CollectionUtils.isEmpty(partitionDataSourceNames)){
						partitionDataSourceNames= ctx.getShardingDataSourceRepository().getPartitionDataSourceNames();
					}
					for(String each: partitionDataSourceNames){
						result.add(new SQLExecutionUnit(each,logicSql));
					}
					if(logger.isInfoEnabled()){
						logger.info("Sharding global table result: " + result.toString());
					}
					return result;
				}
			}
			Collection<String> databaseNames;
			//判断租户传递过来的数据库分片是否存在，优先级最高
			HintContext hintContext= HintContextHolder.getHintContext();
			if(hintContext!= null){
				databaseNames= Collections.singletonList(hintContext.getPartitionDBName());
				if(logger.isInfoEnabled()){
					logger.info("Tenant database name: " + hintContext.getPartitionDBName());
				}
			}
			else{
				//然后执行数据库路由
				databaseNames= databaseRouter.doRoute(ctx);
				if(CollectionUtils.isEmpty(databaseNames)){
					//使用默认的数据源
					String name= ctx.getShardingDataSourceRepository().getDefaultDataSource().getName();
					databaseNames= Collections.singletonList(name);
					if(logger.isInfoEnabled()){
						logger.info("will use default database");
					}
				}
				if(logger.isInfoEnabled()){
					logger.info("Sharding database Names: " + databaseNames);
				}
			}
			
			List<SQLExecutionUnit> result= new ArrayList<SQLExecutionUnit>();
			for(String databaseName: databaseNames){
				//最后执行表路由
				Collection<String> sqls= tableRouter.doRoute(ctx,databaseName);
				if(logger.isInfoEnabled()){
					logger.info("Sharding table sqls: " + sqls);
				}
				for(String each: sqls){
					result.add(new SQLExecutionUnit(databaseName,each));
				}
			}
			if(logger.isInfoEnabled()){
				String r="";
				for(SQLExecutionUnit u: result){
					r+= u.toString()+"\n";
				}
				logger.info("SQLExecutionUnit result:\n\n" + r);
			}
			return result;
		}finally{
			ExecuteHolder.clear();
		}
	}

	private SQLStatementType parseStatementType4RW(final String sql){
		Matcher matcher= pattern.matcher(sql);
		if(matcher.find()){
			return SQLStatementType.valueOf(matcher.group().toUpperCase());
		}
		throw new SQLParserException("parse Statement Type error, sql: "+sql);
	}
}
