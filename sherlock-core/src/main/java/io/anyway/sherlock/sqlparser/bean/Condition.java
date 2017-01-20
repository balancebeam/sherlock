package io.anyway.sherlock.sqlparser.bean;

import java.util.ArrayList;
import java.util.List;

public class Condition {
	private final Column column;

	private final BinaryOperator operator;

	private final List<Comparable<?>> values = new ArrayList<Comparable<?>>();

	public Condition(Column column, BinaryOperator operator) {
		this.column = column;
		this.operator = operator;
	}

	public Column getColumn() {
		return this.column;
	}

	public BinaryOperator getOperator() {
		return this.operator;
	}

	public List<Comparable<?>> getValues() {
		return this.values;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Condition@{")
			.append(column.getTableName())
			.append(".").append(column.getColumnName())
			.append(operator.expression)
			.append(values.toString())
			.append("}")	;
		return sb.toString();
	}

	/**
	 * 列对象.
	 * 
	 */
	public static class Column {

		private final String columnName;

		private final String tableName;

		public Column(String columnName, String tableName) {
			this.columnName = columnName;
			this.tableName = tableName;
		}

		public Column() {
			this.columnName = null;
			this.tableName = null;
		}

		public String getColumnName() {
			return this.columnName;
		}

		public String getTableName() {
			return this.tableName;
		}
		
	    @Override
	    public int hashCode(){
	    	return (tableName+"#"+columnName).hashCode();
	    }
	    
	    @Override
	    public boolean equals(Object other){
	    	if(other instanceof Column){
	    		Column c = (Column)other;
	    		if(columnName.equals(c.columnName)&&tableName.equals(c.tableName)){
	    			return true;
	    		}
	    	}
	    	return false;
	    }

		@Override
		public String toString(){
			return "Column@{table="+tableName+",column="+columnName+"}";
		}
	}

	/**
	 * 操作符枚举.
	 * 
	 */
	public enum BinaryOperator {

		EQUAL("="), BETWEEN("BETWEEN"), IN("IN");

		private final String expression;

		BinaryOperator(String expression) {
			this.expression = expression;
		}

		@Override
		public String toString() {
			return expression;
		}
	}
}
