package io.anyway.sherlock.sqlparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.anyway.sherlock.datasource.DatabaseType;
import io.anyway.sherlock.merger.Limit;
import io.anyway.sherlock.util.SQLUtil;
import org.springframework.util.CollectionUtils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import io.anyway.sherlock.sqlparser.bean.AggregationColumn;
import io.anyway.sherlock.sqlparser.bean.Condition;
import io.anyway.sherlock.sqlparser.bean.ConditionContext;
import io.anyway.sherlock.sqlparser.bean.GroupColumn;
import io.anyway.sherlock.sqlparser.bean.OrderColumn;
import io.anyway.sherlock.sqlparser.bean.Table;

public class SQLParsedResult {

	private SQLBuilder sqlBuilder;
	private Set<Table> tables;
	private Table curTable;
	//去重属性
	private boolean distinct;
	//当前的条件上下文
	private final ConditionContext curConditionContext = new ConditionContext();
	//select查询列元数据
	private List<String> metadataColumns= new LinkedList<String>();
	//排序列
	private List<OrderColumn> orderColumns= Collections.<OrderColumn>emptyList();
	//聚合列
	private List<AggregationColumn> aggregationColumns= Collections.<AggregationColumn>emptyList();
	//分组列
	private List<GroupColumn> groupColumns= Collections.<GroupColumn>emptyList();
	//多个条件上下文他们是or的关系ConditionContext or ConditionContext,里面的条件项是and关系 Condition and Condition
	private List<ConditionContext> conditionContexts = new ArrayList<ConditionContext>();
	//rownum alias recording
	private HashSet<String> rownumAlias = new HashSet<String>();
	//查询大小
	private Limit limit;
	
    private boolean hasOrCondition = false;

	public SQLParsedResult(SQLBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
		this.tables = Sets.newHashSet();
	}

	public SQLBuilder getSqlBuilder() {
		return sqlBuilder;
	}
	
	public void setSqlBuilder(SQLBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}

	public Set<Table> getTables() {
		return tables;
	}
	
	public Table getFirstTable(){
		return tables.iterator().next();
	}
	
	public boolean isHasOrCondition() {
		return hasOrCondition;
	}

	public void setHasOrCondition(boolean hasOrCondition) {
		this.hasOrCondition = hasOrCondition;
	}
	
	public HashSet<String> getRownumAlias() {
		return this.rownumAlias;
	}
	
	public List<ConditionContext> getConditions(){
		return this.conditionContexts;
	}

	public ConditionContext getCurCondition(){
		return this.curConditionContext;
	}

	public Table addTable(Table table) {
		this.tables.add(table);
		this.curTable = table;
		return table;
	}
	
	public Table getCurTable(){
		return this.curTable;
	}
	
	public void setCurTable(Table curTable){
		this.curTable = curTable;
	}
	
    public void addCondition(final String columnName, final String tableName, final Condition.BinaryOperator operator, final SQLExpr valueExpr, final DatabaseType databaseType, final List<Object> parameters) {
        Condition.Column column = createColumn(columnName, tableName);

        Comparable<?> value = evalExpression(databaseType, valueExpr, parameters);
        if (null != value) {
            addCondition(column, operator, Collections.<Comparable<?>>singletonList(value));
        }
    }

	public void addCondition(final SQLExpr expr, final Condition.BinaryOperator operator, final List<SQLExpr> valueExprList,
							 final DatabaseType databaseType, final List<Object> parameters) {
		Optional<Condition.Column> column = getColumn(expr);
		if (!column.isPresent()) {
			return;
		}
		List<Comparable<?>> values = new ArrayList<Comparable<?>>(valueExprList.size());
		for (SQLExpr each : valueExprList) {
			Comparable<?> evalValue = evalExpression(databaseType, each, parameters);
			if (null != evalValue) {
				values.add(evalValue);
			}
		}
		if (values.isEmpty()) {
			return;
		}
		addCondition(column.get(), operator, values);
	}

	private void addCondition(final Condition.Column column, final Condition.BinaryOperator operator, final List<Comparable<?>> values) {
		Optional<Condition> optionalCondition = curConditionContext.find(column.getTableName(), column.getColumnName(),
				operator);
		Condition condition;
		// TODO 待讨论
		if (optionalCondition.isPresent()) {
			condition = optionalCondition.get();
		} else {
			condition = new Condition(column, operator);
			curConditionContext.add(condition);
		}
		condition.getValues().addAll(values);
	}

	private Comparable<?> evalExpression(final DatabaseType databaseType, final SQLObject sqlObject,
			final List<Object> parameters) {
		if (sqlObject instanceof SQLMethodInvokeExpr) {
			// TODO 解析函数中的sharingValue不支持
			return null;
		}
		Object result = SQLEvalVisitorUtils.eval(databaseType.name().toLowerCase(), sqlObject, parameters, false);
		if (null == result) {
			return null;
		}
		if (result instanceof Comparable<?>) {
			return (Comparable<?>) result;
		}
		// TODO 对于NULL目前解析为空字符串,此处待考虑解决方法
		return "";
	}

	private Optional<Condition.Column> getColumn(final SQLExpr expr) {
		if (expr instanceof SQLPropertyExpr) {
			return Optional.fromNullable(getColumnWithQualifiedName((SQLPropertyExpr) expr));
		}
		if (expr instanceof SQLIdentifierExpr) {
			return Optional.fromNullable(getColumnWithoutAlias((SQLIdentifierExpr) expr));
		}
		return Optional.absent();
	}

	private Condition.Column getColumnWithQualifiedName(final SQLPropertyExpr expr) {
		Optional<Table> table = findTable(((SQLIdentifierExpr) expr.getOwner()).getName());
		return expr.getOwner() instanceof SQLIdentifierExpr && table.isPresent()
				? createColumn(expr.getName(), table.get().getName()) : null;
	}

	private Condition.Column getColumnWithoutAlias(final SQLIdentifierExpr expr) {
		return null != curTable ? createColumn(expr.getName(), curTable.getName()) : null;
	}

	private Condition.Column createColumn(final String columnName, final String tableName) {
		return new Condition.Column(SQLUtil.getExactlyValue(columnName), SQLUtil.getExactlyValue(tableName));
	}

	private Optional<Table> findTable(final String tableNameOrAlias) {
		Optional<Table> tableFromName = findTableFromName(tableNameOrAlias);
		return tableFromName.isPresent() ? tableFromName : findTableFromAlias(tableNameOrAlias);
	}

	public Optional<Table> findTableFromName(final String name) {
		for (Table each : getTables()) {
			if (each.getName().equalsIgnoreCase(SQLUtil.getExactlyValue(name))) {
				return Optional.of(each);
			}
		}
		return Optional.absent();
	}

	public Optional<Table> findTableFromAlias(final String alias) {
		for (Table each : getTables()) {
			if (each.getAlias().isPresent() && each.getAlias().get().equalsIgnoreCase(SQLUtil.getExactlyValue(alias))) {
				return Optional.of(each);
			}
		}
		return Optional.absent();
	}
	
	public void addMetadataColumn(String name){
		metadataColumns.add(name);
	}
	
	public List<String> getMetadataColumns(){
		return metadataColumns;
	}
	
	public void addOrderColumn(OrderColumn order){
		if(CollectionUtils.isEmpty(orderColumns)){
			orderColumns= new LinkedList<OrderColumn>();
		}
		orderColumns.add(order);
	}
	
	public List<OrderColumn> getOrderColumns(){
		return orderColumns;
	}
	
    public void addAggregationColumn(AggregationColumn column) {
    	if(CollectionUtils.isEmpty(aggregationColumns)){
    		aggregationColumns= new LinkedList<AggregationColumn>();
		}
    	aggregationColumns.add(column);
    }
    
    public List<AggregationColumn> getAggregationColumns(){
    	return aggregationColumns;
    }
    
    public void addGroupColumn(GroupColumn column){
    	if(CollectionUtils.isEmpty(groupColumns)){
    		groupColumns= new LinkedList<GroupColumn>();
		}
    	groupColumns.add(column);
    }
    
    public List<GroupColumn> getGroupColumns(){
    	return groupColumns;
    }
    
    public void setLimit(Limit limit){
    	this.limit= limit;
    }
    
    public Limit getLimit(){
    	return limit;
    }
    
    public void markDistinct(){
    	this.distinct= true;
    }
    
    public boolean distinct(){
    	return distinct;
    }
    
	@Override
	public String toString(){
		return "SQLParsedResult@{\ntables="+tables+",\n"
				+ "conditions="+conditionContexts+",\n"
				+ "distinct="+ distinct+",\n"
				+ "metadataColumns="+metadataColumns+",\n"
				+ "aggregationColumns="+aggregationColumns+",\n"
				+ "orderColumns="+orderColumns+",\n"
				+ "groupColumns="+groupColumns+",\n"
				+ "limit="+limit+",\n"
				+ "sql="+sqlBuilder+"\n}";
	}

}
