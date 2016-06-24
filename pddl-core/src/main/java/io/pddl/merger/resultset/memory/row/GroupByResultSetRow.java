package io.pddl.merger.resultset.memory.row;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.pddl.merger.pipeline.coupling.aggregation.AggregationUnit;
import io.pddl.merger.pipeline.coupling.aggregation.AggregationUnitFactory;
import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.GroupColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 具有分组功能的数据行对象.
 * 
 * @author gaohongtao
 * @author zhangliang
 * @author xiong.j
 */
public final class GroupByResultSetRow extends AbstractResultSetRow {
    
    private final ResultSet resultSet;
    
    private final List<GroupColumn> groupByColumns;
    
    private final Map<AggregationColumn, AggregationUnit> aggregationUnitMap;

    private final static String AUTO_GEN_COL_COUNT = "auto_gen_col_count";
    
    public GroupByResultSetRow(final ResultSet resultSet, final List<GroupColumn> groupByColumns, final List<AggregationColumn> aggregationColumns) throws SQLException {
        super(resultSet);
        this.resultSet = resultSet;
        this.groupByColumns = groupByColumns;
        aggregationUnitMap = Maps.toMap(aggregationColumns, new Function<AggregationColumn, AggregationUnit>() {
            
            @Override
            public AggregationUnit apply(final AggregationColumn input) {
                return AggregationUnitFactory.create(input.getAggregationType());
            }
        });
    }
    
    /**
     * 处理聚合函数结果集.
     * 
     * @throws SQLException SQL异常
     */
    public void aggregate() throws SQLException {
        for (Map.Entry<AggregationColumn, AggregationUnit> each : aggregationUnitMap.entrySet()) {
            each.getValue().merge(getAggregationValues(Collections.singletonList(each.getKey())));
        }
    }
    
    private List<Comparable<?>> getAggregationValues(final List<AggregationColumn> aggregationColumns) throws SQLException {
        List<Comparable<?>> result = new ArrayList<Comparable<?>>(aggregationColumns.size());
        boolean hasAvg = false;
        for (AggregationColumn each : aggregationColumns) {
            result.add((Comparable<?>) resultSet.getObject(each.getColumnIndex()));
            if (AggregationColumn.AggregationType.AVG.equals(each.getAggregationType())) {
                hasAvg = true;
            }
        }
        if (hasAvg) result.add((Comparable<?>) resultSet.getObject(AUTO_GEN_COL_COUNT));
        return result;
    }
    
    /**
     * 生成结果.
     */
    public void generateResult() {
        for (AggregationColumn each : aggregationUnitMap.keySet()) {
            setCell(each.getColumnIndex(), aggregationUnitMap.get(each).getResult());
        }
    }
    
    /**
     * 获取分组值.
     * 
     * @return 分组值集合
     * @throws SQLException SQL异常
     */
    public List<Object> getGroupByValues() throws SQLException {
        List<Object> result = new ArrayList<Object>(groupByColumns.size());
        for (GroupColumn each : groupByColumns) {
            result.add(resultSet.getObject(each.getColumnIndex()));
        }
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("GroupByKey is: ");
        result.append(Lists.transform(groupByColumns, new Function<GroupColumn, Object>() {
            
            @Override
            public Object apply(final GroupColumn input) {
                return getCell(input.getColumnIndex());
            }
        }));
        if (aggregationUnitMap.isEmpty()) {
            return result.toString();
        }
        result.append("; Aggregation result is: ").append(Lists.transform(new ArrayList<AggregationColumn>(aggregationUnitMap.keySet()), new Function<AggregationColumn, String>() {
            
            @Override
            public String apply(final AggregationColumn input) {
                Object value = getCell(input.getColumnIndex());
                value = null == value ? "null" : value;
                return String.format("{index:%d, type:%s, value:%s}", input.getColumnIndex(), input.getAggregationType(), value);
            }
        }));
        return result.toString();
    }
}
