package io.anyway.sherlock.merger;

import java.sql.ResultSet;
import java.util.List;

import io.anyway.sherlock.sqlparser.SQLParsedResult;
import io.anyway.sherlock.sqlparser.bean.GroupColumn;

public class Row{
	
	public Row(){}

	public Row(ResultSet rs,SQLParsedResult parser){
		
	}
	
	public Object getObject(int columnIndex){
		return null;
	}
	public Object getObject(String columnName){
		return null;
	}
	
	public Row clone(List<GroupColumn> groups){
		return new Row();
	}
	
	public void setValue(int columnIndex,Object val){
		
	}
}
