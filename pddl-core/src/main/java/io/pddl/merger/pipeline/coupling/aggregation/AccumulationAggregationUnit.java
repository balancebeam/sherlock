package io.pddl.merger.pipeline.coupling.aggregation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * 累加聚合单元.
 * 
 * @author zhangliang
 */
@RequiredArgsConstructor
@Slf4j
public final class AccumulationAggregationUnit implements AggregationUnit {
    
    private BigDecimal result;
    
    @Override
    public void merge(final List<Comparable<?>> values) {
        if (null == values || null == values.get(0)) {
            return;
        }
        if (null == result) {
            result = new BigDecimal("0");
        }
        result = result.add(new BigDecimal(values.get(0).toString()));
        log.trace("Accumulation result: {}", result.toString());
    }
    
    @Override
    public Comparable<?>  getResult() {
        return result;
    }
}
