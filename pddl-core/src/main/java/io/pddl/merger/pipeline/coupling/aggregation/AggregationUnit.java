package io.pddl.merger.pipeline.coupling.aggregation;

import java.util.List;

/**
 * 归并计算单元接口.
 * 
 * @author gaohongtao
 */
public interface AggregationUnit {
    
    /**
     * 归并聚合值.
     * 
     * @param values 聚合数值
     */
    void merge(List<Comparable<?>> values);
    
    /**
     * 获取计算结果.
     * 
     * @return 计算结果
     */
    Comparable<?> getResult();
}
