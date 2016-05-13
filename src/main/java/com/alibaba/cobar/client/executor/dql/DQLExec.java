package com.alibaba.cobar.client.executor.dql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.cobar.client.datasources.DataSourceDescriptor;
import com.alibaba.cobar.client.executor.util.SqlExecutorUtils;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.statement.DefaultRowHandler;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.scope.StatementScope;

public class DQLExec {
	
	public void executeQuery(StatementScope statementScope,
			DataSourceDescriptor dataSourceDescriptor,
			Connection conn, 
			String sql, 
			Object[] parameters,
			int skipResults, int maxResults, 
			RowHandlerCallback callback,
			CopyOnWriteArrayList<ErrorContext> errorContextList) {

		// ErrorContext errorContext = statementScope.getErrorContext();
		ErrorContext errorContext = new ErrorContext();
		errorContext.setActivity("executing query");
		errorContext.setObjectId(sql);

		PreparedStatement ps = null;
		ResultSet rs = null;
		//set statement ThreadLocal
		SqlExecutorUtils.setupResultObjectFactory(statementScope);

		try {
			errorContext.setMoreInfo("Check the SQL Statement (preparation failed).");

			Integer rsType = statementScope.getStatement().getResultSetType();
			if (rsType != null) {
				ps = SqlExecutorUtils.prepareStatement(statementScope.getSession(), conn, sql, rsType);
			} else {
				ps = SqlExecutorUtils.prepareStatement(statementScope.getSession(), conn, sql);
			}
			//set timeout
			SqlExecutorUtils.setStatementTimeout(statementScope.getStatement(), ps);
			Integer fetchSize = statementScope.getStatement().getFetchSize();
			//set page size
			if (fetchSize != null) {
				ps.setFetchSize(fetchSize.intValue());
			}
			errorContext.setMoreInfo("Check the parameters (set parameters failed).");
			//set parameters
			statementScope.getParameterMap().setParameters(statementScope, ps, parameters);
			errorContext.setMoreInfo("Check the statement (query failed).");

			//record the time before the execution of the SQL statement
			long beginTime = System.currentTimeMillis();
			ps.execute();
			long endTime = System.currentTimeMillis();
			errorContext.setMoreInfo("Check the results (failed to retrieve results).");

			// Begin ResultSet Handling，if concurrent execution
			synchronized (statementScope) {
				rs = handleMultipleResults(ps, statementScope, skipResults, maxResults, callback);
			}
			// End ResultSet Handling
		} catch (SQLException e) {
			errorContext.setCause(e);
			errorContextList.add(errorContext);
		} finally {
			try {
				SqlExecutorUtils.closeResultSet(rs);
			} finally {
				SqlExecutorUtils.closeStatement(statementScope.getSession(), ps);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ResultSet handleMultipleResults(PreparedStatement ps, StatementScope statementScope, int skipResults,
			int maxResults, RowHandlerCallback callback) throws SQLException {
		ResultSet rs;
		rs = getFirstResultSet(statementScope, ps);
		if (rs != null) {
			handleResults(statementScope, rs, skipResults, maxResults, callback);
		}

		// Multiple ResultSet handling
		if (callback.getRowHandler() instanceof DefaultRowHandler) {
			MappedStatement statement = statementScope.getStatement();
			DefaultRowHandler defaultRowHandler = ((DefaultRowHandler) callback.getRowHandler());
			if (statement.hasMultipleResultMaps()) {
				List multipleResults = new ArrayList();
				multipleResults.add(defaultRowHandler.getList());
				ResultMap[] resultMaps = statement.getAdditionalResultMaps();
				int i = 0;
				while (moveToNextResultsSafely(statementScope, ps)) {
					if (i >= resultMaps.length)
						break;
					ResultMap rm = resultMaps[i];
					statementScope.setResultMap(rm);
					rs = ps.getResultSet();
					DefaultRowHandler rh = new DefaultRowHandler();
					handleResults(statementScope, rs, skipResults, maxResults, new RowHandlerCallback(rm, null, rh));
					multipleResults.add(rh.getList());
					i++;
				}
				defaultRowHandler.setList(multipleResults);
				statementScope.setResultMap(statement.getResultMap());
			} else {
				while (moveToNextResultsSafely(statementScope, ps))
					;
			}
		}
		// End additional ResultSet handling
		return rs;
	}

	private void handleResults(StatementScope statementScope, ResultSet rs, int skipResults, int maxResults,
			RowHandlerCallback callback) throws SQLException {
		try {
			statementScope.setResultSet(rs);
			ResultMap resultMap = statementScope.getResultMap();
			if (resultMap != null) {
				// Skip Results
				if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
					if (skipResults > 0) {
						rs.absolute(skipResults);
					}
				} else {
					for (int i = 0; i < skipResults; i++) {
						if (!rs.next()) {
							return;
						}
					}
				}

				// Get Results
				int resultsFetched = 0;
				while ((maxResults == SqlExecutor.NO_MAXIMUM_RESULTS || resultsFetched < maxResults) && rs.next()) {
					Object[] columnValues = resultMap.resolveSubMap(statementScope, rs).getResults(statementScope, rs);
					callback.handleResultObject(statementScope, columnValues, rs);
					resultsFetched++;
				}
			}
		} finally {
			statementScope.setResultSet(null);
		}
	}

	private ResultSet getFirstResultSet(StatementScope scope, Statement stmt) throws SQLException {
		ResultSet rs = null;
		boolean hasMoreResults = true;
		while (hasMoreResults) {
			rs = stmt.getResultSet();
			if (rs != null) {
				break;
			}
			hasMoreResults = moveToNextResultsIfPresent(scope, stmt);
		}
		return rs;
	}

	private boolean moveToNextResultsSafely(StatementScope scope, Statement stmt) throws SQLException {
		if (forceMultipleResultSetSupport(scope) || stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
			return stmt.getMoreResults();
		}
		return false;
	}

	private boolean moveToNextResultsIfPresent(StatementScope scope, Statement stmt) throws SQLException {
		boolean moreResults;
		// This is the messed up JDBC approach for determining if there are more
		// results
		moreResults = !(((moveToNextResultsSafely(scope, stmt) == false) && (stmt.getUpdateCount() == -1)));
		return moreResults;
	}

	private boolean forceMultipleResultSetSupport(StatementScope scope) {
		return ((SqlMapClientImpl) scope.getSession().getSqlMapClient()).getDelegate()
				.isForceMultipleResultSetSupport();
	}
}
