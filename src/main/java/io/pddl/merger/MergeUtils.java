package io.pddl.merger;

import java.sql.ResultSet;
import java.util.List;

import io.pddl.merger.iterator.IteratorResultSet;

public class MergeUtils {

	public static ResultSet mergeResultSet(List<ResultSet> resultSets){
		return new IteratorResultSet(resultSets);
	}
	
	public static int mergeIntegerResult(List<Integer> result){
		int total= 0;
    	for(int it: result){
    		total+= it;
    	}
    	return total;
	}
}
