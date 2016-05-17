package com.alibaba.cobar.client.router;

import com.alibaba.cobar.client.datasources.PartitionDataSource;
import com.ibatis.sqlmap.engine.scope.StatementScope;

public class DefaultCobarTableRouter implements ICobarTableRouter{

	@Override
	public String[] doRoute(StatementScope statementScope, PartitionDataSource partitionDataSource, String sql, Object[] parameters) {
		return new String[]{sql};
	}

}
