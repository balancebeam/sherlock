package io.pddl.merger.pipeline.coupling.aggregation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * 平均值聚合单元.
 * 
 * @author gaohongtao
 */
@RequiredArgsConstructor
@Slf4j
public final class AverageAggregationUnit implements AggregationUnit {
    
    private BigDecimal count;
    
    private BigDecimal sum;
    
    @Override
    public void merge(final List<Comparable<?>> values) {
        if (null == values || null == values.get(0) || null == values.get(1)) {
            return;
        }
        if (null == count) {
            count = new BigDecimal("0");
        }
        if (null == sum) {
            sum = new BigDecimal("0");
        }
        count = count.add(new BigDecimal(values.get(0).toString()));
        sum = sum.add(new BigDecimal(values.get(1).toString()));
        log.trace("AVG result COUNT: {} SUM: {}", count, sum);
    }
    
    @Override
    public Comparable<?> getResult() {
        if (null == count || BigDecimal.ZERO.equals(count)) {
            return count;
        }
        // TODO 通过metadata获取数据库的浮点数精度值
        return sum.divide(count, 4, BigDecimal.ROUND_HALF_UP);
    }
}
