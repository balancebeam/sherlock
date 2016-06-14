package io.pddl.router.support;

public class SQLExecutionUnit {

	private String dataSourceName;
    
    private String shardingSql;
    
    public SQLExecutionUnit(String dataSourceName,String shardingSql){
    	this.dataSourceName= dataSourceName;
    	this.shardingSql= shardingSql;
    }
    
    /**
     * 获取数据源名
     * @return String
     */
    public String getDataSourceName(){
    	return dataSourceName;
    }
    
    /**
     * 获取在某个数据源下表路由后的实际SQL语句
     * @return String
     */
    public String getShardingSql(){
    	return shardingSql;
    }
    
    @Override
    public String toString(){
    	return "{dataSourceName= "+dataSourceName+",shardingSql="+shardingSql+"}";
    }
    
}
