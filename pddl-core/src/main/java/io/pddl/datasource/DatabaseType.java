package io.pddl.datasource;

public enum DatabaseType {
	
	PostgreSQL, MySQL, Oracle, DB2, SQLServer, H2;
	
	//默认是PostgreSQL数据库
	private static DatabaseType applicationDatabaseType= PostgreSQL;
	
	/**
	 * 获取数据库枚举类型 
	 * @return
	 */
	public static DatabaseType getApplicationDatabaseType(){
		return applicationDatabaseType;
	}
	
	/**
	 * 获取数据库枚举类型
	 * @param applicationDatabaseType
	 */
	public static void setApplicationDatabaseType(DatabaseType applicationDatabaseType){
		DatabaseType.applicationDatabaseType= applicationDatabaseType;
	}

}
