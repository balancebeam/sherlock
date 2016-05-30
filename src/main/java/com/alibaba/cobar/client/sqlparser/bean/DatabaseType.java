package com.alibaba.cobar.client.sqlparser.bean;

import com.alibaba.cobar.client.exception.DatabaseTypeUnsupportedException;

public enum DatabaseType {
    
	POSTGRESQL, MySQL;
    
    /**
     * 获取数据库类型枚举.
     * 
     * @param databaseProductName 数据库类型
     * @return 数据库类型枚举
     */
    public static DatabaseType valueFrom(final String databaseProductName) {
        try {
            return DatabaseType.valueOf(databaseProductName);
        } catch (final IllegalArgumentException ex) {
            throw new DatabaseTypeUnsupportedException(databaseProductName);
        }
    }
}