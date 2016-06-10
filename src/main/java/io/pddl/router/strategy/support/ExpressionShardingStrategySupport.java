package io.pddl.router.strategy.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mvel2.MVEL;

import io.pddl.executor.ExecuteHolder;

public class ExpressionShardingStrategySupport extends AbstractSingleShardingStrategy<Integer>{

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
	public String doEqualSharding(Collection<String> availableNames, String column,Integer value) {
		@SuppressWarnings("unchecked")
		Map<String,Integer> expressionContext= (Map<String,Integer>)ExecuteHolder.getAttribute("DefaultExpressionStrategy");
		if(expressionContext== null){
			//cache the context
			ExecuteHolder.setAttribute("DefaultExpressionStrategy", expressionContext= new HashMap<String,Integer>());
		}
		expressionContext.put(column, value);
		StringBuilder builder= new StringBuilder();
		builder.append(prefix)
			.append(MVEL.eval(expression, expressionContext))
			.append(postfix);
		return builder.toString();
	}

	@Override
	public Collection<String> doInSharding(Collection<String> availableNames, String column, List<Integer> values) {
		Set<String> result= new HashSet<String>();
		for(Integer value: values){
			result.add(doEqualSharding(availableNames,column,value));
		}
		return result;
	}

	@Override
	public Collection<String> doBetweenSharding(Collection<String> availableNames, String column, Integer lower, Integer upper) {
		Set<String> result= new HashSet<String>();
		for(Integer value=lower;value<=upper;value++){
			result.add(doEqualSharding(availableNames,column,value));
		}
		return result;
	}
}
