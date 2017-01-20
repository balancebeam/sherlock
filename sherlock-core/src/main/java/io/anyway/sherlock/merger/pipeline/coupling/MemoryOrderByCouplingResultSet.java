package io.anyway.sherlock.merger.pipeline.coupling;

import io.anyway.sherlock.merger.resultset.memory.AbstractMemoryOrderByResultSet;
import io.anyway.sherlock.merger.MergeContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

/**
 * 根据排序列内存排序的连接结果集.
 *
 * @author xiong.j
 */
public final class MemoryOrderByCouplingResultSet extends AbstractMemoryOrderByResultSet {
    
    public MemoryOrderByCouplingResultSet(final ResultSet resultSet, final MergeContext mergeContext) throws SQLException {
        super(Collections.singletonList(resultSet), mergeContext.getOrderColumns());
    }
}
