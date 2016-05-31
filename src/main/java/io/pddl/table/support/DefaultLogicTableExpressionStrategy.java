package io.pddl.table.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mvel2.MVEL;

import io.pddl.table.LogicTable;

public class DefaultLogicTableExpressionStrategy extends AbstractSingleLogicTableStrategy<Long>{

	private String expression;
	
	private String prefix= "";
	
	private String postfix= "";
	
	public void setExpression(String expression){
		String[] vars= (" "+expression+" ").split("\\$\\{|\\}");
		switch(vars.length){
			case 1:
				this.expression= vars[1].trim();
				break;
			case 3:
				this.prefix= vars[0].trim();
				this.expression= vars[1].trim();
				this.postfix= vars[2].trim();
		}
	}
	
	@Override
	public String doEqualSharding(LogicTable table, String column,Long value) {
		StringBuilder builder= new StringBuilder();
		builder.append(prefix)
			.append(MVEL.eval(expression, value))
			.append(postfix);
		return table.getName().concat(builder.toString());
	}

	@Override
	public Collection<String> doInSharding(LogicTable table, String column, List<Long> values) {
		Set<String> result= new HashSet<String>();
		for(Long value: values){
			result.add(doEqualSharding(table,column,value));
		}
		return result;
	}

	@Override
	public Collection<String> doBetweenSharding(LogicTable table, String column, Long lower, Long upper) {
		Set<String> result= new HashSet<String>();
		for(long value=lower;value<=upper;value++){
			result.add(doEqualSharding(table,column,value));
		}
		return result;
	}


}
