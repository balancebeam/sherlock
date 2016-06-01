package io.pddl.router.table.strategy.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mvel2.MVEL;

import io.pddl.executor.ExecutorContext;
import io.pddl.executor.ExecutorContextHolder;
import io.pddl.executor.support.ExecutorContextSupport;
import io.pddl.router.table.LogicTable;

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
	
	@SuppressWarnings("unchecked")
	@Override
	public String doEqualSharding(LogicTable table, String column,Long value) {
		StringBuilder builder= new StringBuilder();
		ExecutorContext executorContext= ExecutorContextHolder.getContext();
		Map<String,Long> expressionContext= (Map<String,Long>)executorContext.getAttribute("ExpressionContext");
		if(expressionContext== null){
			expressionContext= new HashMap<String,Long>();
			((ExecutorContextSupport)executorContext).setAttribute("ExpressionContext",expressionContext);
		}
		expressionContext.put(column, value);
		builder.append(prefix)
			.append(MVEL.eval(expression, expressionContext))
			.append(postfix);
		return builder.toString();
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
