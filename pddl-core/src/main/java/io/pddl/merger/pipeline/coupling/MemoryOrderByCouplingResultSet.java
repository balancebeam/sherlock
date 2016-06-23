package io.pddl.merger.pipeline.coupling;

import io.pddl.merger.MergeContext;
import io.pddl.merger.resultset.memory.AbstractMemoryOrderByResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

/**
 * 根据排序列内存排序的连接结果集.
 *
 * @author gaohongtao
 * @author zhangliang
 * @author xiong.j
 */
public final class MemoryOrderByCouplingResultSet extends AbstractMemoryOrderByResultSet {
    
    public MemoryOrderByCouplingResultSet(final ResultSet resultSet, final MergeContext mergeContext) throws SQLException {
        super(Collections.singletonList(resultSet), mergeContext.getOrderColumns());
    }
}
