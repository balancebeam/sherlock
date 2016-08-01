package io.pddl.merger;

public class Limit {

	private int offset;

	private int rowCount;
	
	private int upperBound;
	
	public Limit(int offset,int rowCount){
		this.offset= offset;
		this.rowCount= rowCount;
		this.upperBound = -1;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getRowCount(){
		if (rowCount == -1)
			return upperBound - offset;
		else 
		    return rowCount;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	
	public void setUpperBound(int upBound) {
		this.upperBound = upBound;
	}
	
	public int getUpperBound() {
		return this.upperBound;
	}
	
	@Override
	public String toString(){
		return "{offset="+offset+",rowCount="+rowCount+"}";
	}
}
