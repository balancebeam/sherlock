package com.alibaba.cobar.client.router;

import com.alibaba.cobar.client.datasources.DataSourceDescriptor;
import com.ibatis.sqlmap.engine.scope.StatementScope;

public interface ICobarTableRouter {
	
	String[] doRoute(StatementScope statementScope,DataSourceDescriptor dataSourceDescriptor,String sql,Object[] parameters);
}
