package io.pddl.datasource;

public enum DatabaseType {
	
	PostgreSQL, MySQL, Oracle, DB2, SQLServer, H2;
	
	private static DatabaseType applicationDatabaseType= PostgreSQL;
	
	public static DatabaseType getApplicationDatabaseType(){
		return applicationDatabaseType;
	}
	
	public static void setApplicationDatabaseType(DatabaseType applicationDatabaseType){
		DatabaseType.applicationDatabaseType= applicationDatabaseType;
	}

}
