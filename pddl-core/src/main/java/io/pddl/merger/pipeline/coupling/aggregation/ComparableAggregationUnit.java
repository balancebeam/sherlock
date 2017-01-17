package io.pddl.merger.pipeline.coupling.aggregation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 比较聚合单元.
 * 
 */
@RequiredArgsConstructor
@Slf4j
public final class ComparableAggregationUnit implements AggregationUnit {
    
    private final boolean asc;
    
    private Comparable<?> result;
    
    @SuppressWarnings("unchecked")
    @Override
    public void merge(final List<Comparable<?>> values) {
        if (null == values || null == values.get(0)) {
            return;
        }
        if (null == result) {
            result = values.get(0);
            log.trace("Comparable result: {}", result);
            return;
        }
        @SuppressWarnings("rawtypes")
		int comparedValue = ((Comparable) values.get(0)).compareTo(result);
        if (asc && comparedValue < 0 || !asc && comparedValue > 0) {
            result = values.get(0);
            log.trace("Comparable result: {}", result);
        }
    }
    
    @Override
    public Comparable<?> getResult() {
        return result;
    }
}
