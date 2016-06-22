package io.pddl.merger.pipeline.coupling;

import io.pddl.merger.Limit;
import io.pddl.merger.MergeContext;
import io.pddl.merger.resultset.delegate.AbstractDelegateResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

/**
 * 分页限制条件的连接结果集.
 * 
 * @author xiong.j
 */
public final class LimitCouplingResultSet extends AbstractDelegateResultSet {
    
    private final Limit limit;
    
    private int rowNumber;
    
    public LimitCouplingResultSet(final ResultSet resultSet, final MergeContext mergeContext) throws SQLException {
        super(Collections.singletonList(resultSet));
        limit = mergeContext.getLimit();
    }
    
    @Override
    protected boolean firstNext() throws SQLException {
        return skipOffset() && doNext();
    }
    
    private boolean skipOffset() throws SQLException {
        for (int i = 0; i < limit.getOffset(); i++) {
            if (!getDelegate().next()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected boolean afterFirstNext() throws SQLException {
        return doNext();
    }
    
    private boolean doNext() throws SQLException {
        return ++rowNumber <= limit.getRowCount() && getDelegate().next();
    }
}
