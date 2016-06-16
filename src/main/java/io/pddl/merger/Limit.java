package io.pddl.merger;

public class Limit {

	private int offset;

	private int rowCount;
	
	public Limit(int offset,int rowCount){
		this.offset= offset;
		this.rowCount= rowCount;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getRowCount(){
		return rowCount;
	}
	
	@Override
	public String toString(){
		return "{offset="+offset+",rowCount="+rowCount+"}";
	}
}
