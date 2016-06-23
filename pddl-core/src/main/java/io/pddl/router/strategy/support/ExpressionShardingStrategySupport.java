package io.pddl.router.strategy.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mvel2.MVEL;

import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteHolder;

public class ExpressionShardingStrategySupport extends AbstractSingleColumnShardingStrategy<Integer>{

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
		if(logger.isInfoEnabled()){
			logger.info("{prefix="+prefix+",expression="+expression+",postfix="+postfix+"}");
		}
	}
	
	@Override
	public String doEqualSharding(ExecuteContext ctx,Collection<String> availableNames, String column,Integer value) {
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
		
		if(logger.isInfoEnabled()){
			logger.info("{context="+expressionContext+",result="+builder.toString()+"}");
		}
		return builder.toString();
	}

	@Override
	public Collection<String> doInSharding(ExecuteContext ctx,Collection<String> availableNames, String column, List<Integer> values) {
		Set<String> result= new HashSet<String>();
		for(Integer value: values){
			result.add(doEqualSharding(ctx,availableNames,column,value));
		}
		return result;
	}

	@Override
	public Collection<String> doBetweenSharding(ExecuteContext ctx,Collection<String> availableNames, String column, Integer lower, Integer upper) {
		Set<String> result= new HashSet<String>();
		for(Integer value=lower;value<=upper;value++){
			result.add(doEqualSharding(ctx,availableNames,column,value));
		}
		return result;
	}
}
