package io.anyway.sherlock.merger.pipeline.coupling.aggregation;

import io.anyway.sherlock.sqlparser.bean.AggregationColumn;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 聚合函数结果集归并单元工厂.
 * 
 * @author xiong.j
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AggregationUnitFactory {
    
    public static AggregationUnit create(final AggregationColumn.AggregationType type) {
        if (type.equals(AggregationColumn.AggregationType.MAX)) {
            return new ComparableAggregationUnit(false);
        } else if (type.equals(AggregationColumn.AggregationType.MIN)) {
            return new ComparableAggregationUnit(true);
        } else if (type.equals(AggregationColumn.AggregationType.SUM)) {
            return new AccumulationAggregationUnit();
        } else if (type.equals(AggregationColumn.AggregationType.COUNT)) {
            return new AccumulationAggregationUnit();
        } else if (type.equals(AggregationColumn.AggregationType.AVG)) {
            return new AverageAggregationUnit();
        } else {
            throw new UnsupportedOperationException(type.toString());
        }
    }
}
