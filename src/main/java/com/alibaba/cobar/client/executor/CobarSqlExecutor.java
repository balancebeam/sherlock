package com.alibaba.cobar.client.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.alibaba.cobar.client.datasources.CobarDataSourceDescriptor;
import com.alibaba.cobar.client.executor.dml.DMLExec;
import com.alibaba.cobar.client.executor.dql.DQLExec;
import com.alibaba.cobar.client.router.ICobarTableRouter;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.scope.StatementScope;

/**
 * 
 * @author yangzz
 *
 */
public class CobarSqlExecutor extends SqlExecutor {
	
	final Log logger= LogFactory.getLog(CobarSqlExecutor.class);
	
	final private DQLExec dqlExec;
	
	final private DMLExec dmlExec;
	
	//table router executor for sql
	private ICobarTableRouter tableRouter;
	
	private Map<String, ExecutorService> dataSourceExcutors;
	
	public CobarSqlExecutor(){
		dqlExec= new DQLExec();
		dmlExec= new DMLExec();
	}
	
	public void setTableRouter(ICobarTableRouter tableRouter){
		this.tableRouter= tableRouter;
	}
	
	public void setDataSourceExcutors(Map<String, ExecutorService> dataSourceExcutors){
		this.dataSourceExcutors= dataSourceExcutors;
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
		final CobarDataSourceDescriptor dataSourceDescriptor= executorContext.getDataSourceDescriptor();
		//trace the query error message for more thread
		final CopyOnWriteArrayList<ErrorContext> errorContextList= new CopyOnWriteArrayList<>();
		//route sql by sharding table strategy，including ER
		String[] routerSqls= doTableRoute(statementScope,dataSourceDescriptor,sql,parameters);
		//if one record or in transaction
		if(routerSqls.length== 1 || !executorContext.isReadable()){
			for(String item: routerSqls){
				dqlExec.executeQuery(statementScope,
						dataSourceDescriptor,
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
					dataSourceDescriptor,
					connection,
					routerSqls[0],
					parameters,
					skipResults,
					maxResults,
					callback,
					errorContextList);
			ExecutorService executorService= dataSourceExcutors.get(dataSourceDescriptor.getIdentity());
			//use new thread pool execute other query job
			for(int i=1;i<routerSqls.length;i++){
				final String tableRoutedSql= routerSqls[i];
				executorService.execute(new Runnable(){
					@Override
					public void run() {
						DataSource dataSource= dataSourceDescriptor.getReadDataSource();
						Connection conn= null;
						try{
							conn= DataSourceUtils.getConnection(dataSource);
							dqlExec.executeQuery(statementScope,
									dataSourceDescriptor,
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
								logger.error("Close connection error for "+dataSourceDescriptor.getIdentity()+" DataSource",e);
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
		CobarDataSourceDescriptor dataSourceDescriptor= executorContext.getDataSourceDescriptor();
		
	    String[] routerSqls= doTableRoute(statementScope,dataSourceDescriptor,sql,parameters);
	    int rows = 0;
	    //dml operation always use the same connection,whether or not including the transaction
	    for(String rSql: routerSqls){
	    	rows+= dmlExec.executeUpdate(statementScope,dataSourceDescriptor,conn, rSql, parameters);
	    }
	    return rows;
	}
	
	private String[] doTableRoute(StatementScope statementScope,CobarDataSourceDescriptor dataSourceDescriptor,String sql,Object[] parameters){
		if(null== tableRouter){
			return new String[]{sql};
		}
		return tableRouter.doRoute(statementScope,dataSourceDescriptor,sql,parameters);
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
