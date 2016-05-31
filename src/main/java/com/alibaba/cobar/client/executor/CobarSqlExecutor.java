package com.alibaba.cobar.client.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.alibaba.cobar.client.CobarSqlMapClientTemplate;
import com.alibaba.cobar.client.datasources.PartitionDataSource;
import com.alibaba.cobar.client.executor.dml.DMLExec;
import com.alibaba.cobar.client.executor.dql.DQLExec;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.scope.StatementScope;

import io.pddl.table.LogicTableRouter;

/**
 * {@link CobarSqlExecutor} is an extension to ibatis's default
 * {@link SqlExecutor}, it works as the important component of <i>Cobar
 * ClientX</i> product.<br>
 * We mainly override public method before executing DQL or DML SQL
 * {@link CobarSqlExecutor} for situations when you have to partition
 * you table to enable horizontal scalability(scale out the system). but we
 * still keep the default behaviors of {@link SqlExecutor} untouched if
 * you still need them.<br> {@link CobarSqlExecutor} usually can monitor and statistic
 * SQL executing situation, including execution time、execution count、fail count
 * {@link DQLExec} query
 * {@link DMLExec} insert update delete batch
 * 
 * @author yangzz
 * @version 0.0.1-SNAPSHOT
 */
public class CobarSqlExecutor extends SqlExecutor {
	
	final Log logger= LogFactory.getLog(CobarSqlExecutor.class);
	
	final private DQLExec dqlExec;
	
	final private DMLExec dmlExec;
	
	//table router executor for sql
	private LogicTableRouter tableRouter;
	
	private CobarSqlMapClientTemplate template;
	
	public CobarSqlExecutor(){
		dqlExec= new DQLExec();
		dmlExec= new DMLExec();
	}
	
	public void setTableRouter(LogicTableRouter tableRouter){
		this.tableRouter= tableRouter;
	}
	
	public void setCobarSqlMapClientTemplate(CobarSqlMapClientTemplate template){
		this.template= template;
	}

	@Override
	public void executeQuery(final StatementScope statementScope, 
			final Connection connection, 
			final String sql, 
			final Object[] parameters, 
			final int skipResults, 
			final int maxResults, 
			final RowHandlerCallback callback) throws SQLException {
		//fetch the execute context from current thread local
		IExecutorContext executorContext= ExecutorContextHolder.getExecutorContext();
		//get the connection belonging dataSource 
		final PartitionDataSource partitionDataSource= executorContext.getPartitionDataSource();
		//trace the query error message for more thread
		final CopyOnWriteArrayList<ErrorContext> errorContextList= new CopyOnWriteArrayList<ErrorContext>();
		//route sql by sharding table strategy，including ER
		String[] routerSqls= doTableRoute(sql,parameters);
		//if one record or in transaction
		if(routerSqls.length== 1 || !connection.getAutoCommit()){
			for(String item: routerSqls){
				dqlExec.executeQuery(statementScope,
						partitionDataSource,
						connection,
						item,
						parameters,
						skipResults,
						maxResults,
						callback,
						errorContextList);
			}
		}
		else{
			//main thread will wait until all job have been completed
			final CountDownLatch latchs= new CountDownLatch(routerSqls.length- 1);
			//main tread execute the first query job
			dqlExec.executeQuery(statementScope,
					partitionDataSource,
					connection,
					routerSqls[0],
					parameters,
					skipResults,
					maxResults,
					callback,
					errorContextList);
			//get ExecutorService from CobarSqlMapClientTemplate by partition id
			ExecutorService executorService= template.getDataSourceSpecificExecutors().get(partitionDataSource.getName());
			//use new thread pool execute other query job
			for(int i=1;i<routerSqls.length;i++){
				final String tableRoutedSql= routerSqls[i];
				executorService.execute(new Runnable(){
					@Override
					public void run() {
						DataSource dataSource= partitionDataSource.getReadDataSource();
						Connection conn= null;
						try{
							conn= DataSourceUtils.getConnection(dataSource);
							dqlExec.executeQuery(statementScope,
									partitionDataSource,
									conn,
									tableRoutedSql,
									parameters,
									skipResults,
									maxResults,
									callback,
									errorContextList);
						}
						catch(CannotGetJdbcConnectionException e){
							//keep the exception
							ErrorContext errorContext= new ErrorContext();
							errorContext.setActivity("executing query");
							errorContext.setObjectId(tableRoutedSql);
							errorContext.setMoreInfo("Get a connection failure.");
							errorContext.setCause(e);
							errorContextList.add(errorContext);
						}
						finally{
							try {
								DataSourceUtils.doReleaseConnection(conn,dataSource);
							} catch (SQLException e) {
								logger.error("Close connection error for "+partitionDataSource.getName()+" DataSource",e);
							}
							latchs.countDown();
						}
					}
				});
			}
			try {
				latchs.await();
			} catch (InterruptedException e) {
				logger.error("Execute concurrent sql("+sql+") was Interrupted",e);
			}
		}
		//if exists exception
		if(!errorContextList.isEmpty()){
			ErrorContext errorContext = statementScope.getErrorContext(),
				firstErrorContext= errorContextList.get(0);
			
			String objectId= firstErrorContext.getObjectId();
			String moreInfo= firstErrorContext.getMoreInfo();
			SQLException cause= (SQLException)firstErrorContext.getCause();
			for(int i=1;i< errorContextList.size();i++){
				ErrorContext err= errorContextList.get(i);
				objectId+=","+err.getObjectId();
				moreInfo+=","+err.getMoreInfo();
				cause.setNextException((SQLException)err.getCause());
				cause= (SQLException)err.getCause();
			}
			errorContext.setObjectId(objectId);
			errorContext.setMoreInfo(moreInfo);
			errorContext.setCause(firstErrorContext.getCause());
			throw (SQLException)firstErrorContext.getCause();
		}
	}
	
	@Override
	public int executeUpdate(final StatementScope statementScope,
			final Connection conn, 
			final String sql, 
			final Object[] parameters) throws SQLException {
		
		IExecutorContext executorContext= ExecutorContextHolder.getExecutorContext();
		PartitionDataSource partitionDataSource= executorContext.getPartitionDataSource();
		
	    String[] routerSqls= doTableRoute(sql,parameters);
	    int rows = 0;
	    //dml operation always use the same connection,whether or not including the transaction
	    for(String rSql: routerSqls){
	    	rows+= dmlExec.executeUpdate(statementScope,partitionDataSource,conn, rSql, parameters);
	    }
	    return rows;
	}
	
	private String[] doTableRoute(String sql,Object[] parameters){
		if(null== tableRouter){
			return new String[]{sql};
		}
		return tableRouter.doRoute(sql,parameters);
	}
//	
//	@Override
//	public void addBatch(StatementScope statementScope, Connection conn, String sql, Object[] parameters) throws SQLException {
//		super.addBatch(statementScope, conn, sql, parameters);
//	}
//	
//	@Override
//	public int executeBatch(SessionScope sessionScope) throws SQLException {
//		return super.executeBatch(sessionScope);
//	}
//	
//	@Override
//	public List<?> executeBatchDetailed(SessionScope sessionScope) throws SQLException, BatchException {
//		return super.executeBatchDetailed(sessionScope);
//	}
//	
//	@Override
//	public int executeUpdateProcedure(StatementScope statementScope, Connection conn, String sql, Object[] parameters) throws SQLException {
//		return super.executeUpdateProcedure(statementScope, conn, sql, parameters);
//	}
//	
//	@Override
//	public void executeQueryProcedure(StatementScope statementScope, Connection conn, String sql, Object[] parameters, int skipResults, int maxResults, RowHandlerCallback callback) throws SQLException {
//		super.executeQueryProcedure(statementScope, conn, sql, parameters, skipResults, maxResults, callback);
//	}
//	
//	@Override
//	public void cleanup(SessionScope sessionScope) {
//	    super.cleanup(sessionScope);
//	}
}
