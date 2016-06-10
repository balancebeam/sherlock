package io.pddl.router.support;

public class SQLExecutionUnit {

	private String dataSourceName;
    
    private String shardingSql;
    
    public SQLExecutionUnit(String dataSourceName,String shardingSql){
    	this.dataSourceName= dataSourceName;
    	this.shardingSql= shardingSql;
    }
    
    public String getDataSourceName(){
    	return dataSourceName;
    }
    
    public String getShardingSql(){
    	return shardingSql;
    }
    
}
