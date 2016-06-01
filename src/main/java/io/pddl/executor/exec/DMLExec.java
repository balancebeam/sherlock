package io.pddl.executor.exec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.scope.StatementScope;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.executor.util.SqlExecutorUtils;

public class DMLExec {

	public int executeUpdate(StatementScope statementScope, 
			PartitionDataSource partitionDataSource,
			Connection conn, 
			String sql,
			Object[] parameters) throws SQLException {

		ErrorContext errorContext = statementScope.getErrorContext();
		errorContext.setActivity("executing update");
		PreparedStatement ps = null;
		SqlExecutorUtils.setupResultObjectFactory(statementScope);

		try {
			errorContext.setObjectId(sql);
			errorContext.setMoreInfo("Check the SQL Statement (preparation failed).");
			ps = SqlExecutorUtils.prepareStatement(statementScope.getSession(), conn, sql);
			SqlExecutorUtils.setStatementTimeout(statementScope.getStatement(), ps);
			errorContext.setMoreInfo("Check the parameters (set parameters failed).");
			statementScope.getParameterMap().setParameters(statementScope, ps, parameters);
			errorContext.setMoreInfo("Check the statement (update failed).");
			//record the time before the execution of the SQL statement
			long beginTime = System.currentTimeMillis();
			ps.execute();
			long endTime = System.currentTimeMillis();
			return ps.getUpdateCount();
		}catch(SQLException e){
			//the exception will handle with future distributed transaction
			throw e;
		}
		finally {
			SqlExecutorUtils.closeStatement(statementScope.getSession(), ps);
		}
	}

}
