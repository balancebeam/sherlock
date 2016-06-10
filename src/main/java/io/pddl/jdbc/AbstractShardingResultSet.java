/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.pddl.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.util.CollectionUtils;

import io.pddl.jdbc.adapter.AbstractResultSetAdapter;
import io.pddl.merger.Limit;

/**
 * 支持分片的结果集抽象类.
 * 
 * @author zhangliang
 */
public abstract class AbstractShardingResultSet extends AbstractResultSetAdapter {
    
    private final Limit limit;
    
    private boolean offsetSkipped;
    
    private int readCount;
    
    protected AbstractShardingResultSet(final List<ResultSet> resultSets, final Limit limit) {
        super(resultSets);
        this.limit = limit;
        if(!CollectionUtils.isEmpty(resultSets)){
        	setCurrentResultSet(resultSets.get(0));
        }
    }
    
    @Override
    public final boolean next() throws SQLException {
        if (null != limit && !offsetSkipped) {
            skipOffset();
        }
        return null == limit ? nextForSharding() : ++readCount <= limit.getRowCount() && nextForSharding();
    }
    
    private void skipOffset() {
        for (int i = 0; i < limit.getOffset(); i++) {
            try {
                if (!nextForSharding()) {
                    break;
                }
            } catch (final SQLException ignored) {
//                log.warn("Skip result set error", ignored);
            }
        }
        offsetSkipped = true;
    }
    
    /**
     * 迭代结果集.
     * 
     * @return true 可以继续访问 false 不能继续访问
     * @throws SQLException
     */
    protected abstract boolean nextForSharding() throws SQLException;
}
