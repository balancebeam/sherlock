package io.pddl.sqlparser.bean;

public class AggregationColumn  implements IndexColumn{

	public enum AggregationType {
        MAX, MIN, SUM, COUNT, AVG;
    }
	
	private String expression;
	
	private int columnIndex;
	
	private AggregationType aggregationType;

	public AggregationColumn(String expression,int columnIndex,AggregationType aggregationType){
		this.expression= expression;
		this.columnIndex= columnIndex;
		this.aggregationType= aggregationType;
	}
	
	public AggregationType getAggregationType(){
		return aggregationType;
	} 
	
	public String getExpression(){
		return expression;
	}
	
	public int getColumnIndex(){
		return columnIndex;
	}
	
	
	@Override
	public String toString(){
		return "{expression="+expression+",columnIndex="+columnIndex+"}";
	}
	
}
