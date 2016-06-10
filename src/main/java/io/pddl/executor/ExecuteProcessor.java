package io.pddl.executor;

import java.util.List;

public interface ExecuteProcessor {
	
	<IN, OUT> List<OUT> execute(ExecuteContext ctx,List<InputWrapper<IN>> inputs,ExecuteUnit<IN, OUT> executeUnit);
	
}
