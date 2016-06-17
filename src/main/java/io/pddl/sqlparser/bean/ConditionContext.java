package io.pddl.sqlparser.bean;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Optional;

import io.pddl.sqlparser.bean.Condition.BinaryOperator;
import io.pddl.sqlparser.bean.Condition.Column;

public final class ConditionContext {
    
    private final Map<Column, Condition> conditions = new LinkedHashMap<Column, Condition>();
    
    /**
     * 添加条件对象.
     * 
     * @param condition 条件对象
     */
    public void add(final Condition condition) {
        // TODO 自关联有问题，表名可考虑使用别名对应
        conditions.put(condition.getColumn(), condition);
    }
    
    /**
     * 查找条件对象.
     * 
     * @param table 表名称
     * @param column 列名称
     * @return 条件对象
     */
    public Optional<Condition> find(final String table, final String column) {
        return Optional.fromNullable(conditions.get(new Column(column, table)));
    }
    
    public Collection<Condition> getAllCondition(){
    	return this.conditions.values();
    }
    
    
    /**
     * 查找条件对象.
     * 
     * @param table 表名称
     * @param column 列名称
     * @param operator 操作符
     * @return 条件对象
     */
    public Optional<Condition> find(final String table, final String column, final BinaryOperator operator) {
        Optional<Condition> result = find(table, column);
        if (!result.isPresent()) {
            return result;
        }
        return result.get().getOperator() == operator ? result : Optional.<Condition>absent();
    }
    
    public boolean isEmpty() {
        return conditions.isEmpty();
    }
    
    public void clear() {
        conditions.clear();
    }
    
    @Override
    public String toString(){
    	return conditions.toString();
    }
    
}
