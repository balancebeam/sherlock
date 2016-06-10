package io.pddl.merger;

import java.util.List;

public interface MergeUnit<IN, OUT> {
    
    OUT merge(final List<IN> params);
}
