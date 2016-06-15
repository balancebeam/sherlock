package io.pddl.sqlparser.bean;

public class AggregationColumn{

	public enum AggregationType {
        MAX, MIN, SUM, COUNT, AVG;
    }
	
	private String expression;
	
	private String alias;
	
	private AggregationType aggregationType;

	public AggregationColumn(String expression,String alias,AggregationType aggregationType){
		this.expression= expression;
		this.alias= alias;
		this.aggregationType= aggregationType;
	}
	
	public AggregationType getAggregationType(){
		return aggregationType;
	} 
	
	public String getExpression(){
		return expression;
	}
	
	public String getAlias(){
		return alias;
	}
	
}
