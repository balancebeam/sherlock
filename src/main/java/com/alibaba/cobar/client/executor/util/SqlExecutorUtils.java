package com.alibaba.cobar.client.executor.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.result.ResultObjectFactoryUtil;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;

public abstract class SqlExecutorUtils {

	public static void setupResultObjectFactory(StatementScope statementScope) {
		SqlMapClientImpl client = (SqlMapClientImpl) statementScope.getSession().getSqlMapClient();
		ResultObjectFactoryUtil.setResultObjectFactory(client.getResultObjectFactory());
		ResultObjectFactoryUtil.setStatementId(statementScope.getStatement().getId());
	}

	public static void setStatementTimeout(MappedStatement mappedStatement, Statement statement) throws SQLException {
		if (mappedStatement.getTimeout() != null) {
			statement.setQueryTimeout(mappedStatement.getTimeout().intValue());
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
	}

	public static void closeStatement(SessionScope sessionScope, PreparedStatement ps) {
		if (ps != null) {
			if (!sessionScope.hasPreparedStatement(ps)) {
				try {
					ps.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}

	public static PreparedStatement prepareStatement(SessionScope sessionScope, Connection conn, String sql,
			Integer rsType) throws SQLException {
		SqlMapExecutorDelegate delegate = ((SqlMapClientImpl) sessionScope.getSqlMapExecutor()).getDelegate();
		if (sessionScope.hasPreparedStatementFor(sql)) {
			return sessionScope.getPreparedStatement((sql));
		} else {
			PreparedStatement ps = conn.prepareStatement(sql, rsType.intValue(), ResultSet.CONCUR_READ_ONLY);
			sessionScope.putPreparedStatement(delegate, sql, ps);
			return ps;
		}
	}

	public static CallableStatement prepareCall(SessionScope sessionScope, Connection conn, String sql, Integer rsType)
			throws SQLException {
		SqlMapExecutorDelegate delegate = ((SqlMapClientImpl) sessionScope.getSqlMapExecutor()).getDelegate();
		if (sessionScope.hasPreparedStatementFor(sql)) {
			return (CallableStatement) sessionScope.getPreparedStatement((sql));
		} else {
			CallableStatement cs = conn.prepareCall(sql, rsType.intValue(), ResultSet.CONCUR_READ_ONLY);
			sessionScope.putPreparedStatement(delegate, sql, cs);
			return cs;
		}
	}

	public static PreparedStatement prepareStatement(SessionScope sessionScope, Connection conn, String sql)
			throws SQLException {
		SqlMapExecutorDelegate delegate = ((SqlMapClientImpl) sessionScope.getSqlMapExecutor()).getDelegate();
		if (sessionScope.hasPreparedStatementFor(sql)) {
			return sessionScope.getPreparedStatement((sql));
		} else {
			PreparedStatement ps = conn.prepareStatement(sql);
			sessionScope.putPreparedStatement(delegate, sql, ps);
			return ps;
		}
	}

	public static CallableStatement prepareCall(SessionScope sessionScope, Connection conn, String sql)
			throws SQLException {
		SqlMapExecutorDelegate delegate = ((SqlMapClientImpl) sessionScope.getSqlMapExecutor()).getDelegate();
		if (sessionScope.hasPreparedStatementFor(sql)) {
			return (CallableStatement) sessionScope.getPreparedStatement((sql));
		} else {
			CallableStatement cs = conn.prepareCall(sql);
			sessionScope.putPreparedStatement(delegate, sql, cs);
			return cs;
		}
	}
}
