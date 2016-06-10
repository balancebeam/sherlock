package io.pddl.sqlparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import io.pddl.datasource.DatabaseType;
import io.pddl.sqlparser.bean.Condition;
import io.pddl.sqlparser.bean.Condition.BinaryOperator;
import io.pddl.sqlparser.bean.Condition.Column;
import io.pddl.sqlparser.bean.ConditionContext;
import io.pddl.sqlparser.bean.Table;
import io.pddl.util.SQLUtil;

public class SQLParsedResult {

	private SQLBuilder sqlBuilder;
	private Set<Table> tables;
	private Table curTable;
	private final ConditionContext curConditionContext = new ConditionContext();

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
	
	public ConditionContext getCondition(){
		return this.curConditionContext;
	}

	public Table addTable(Table table) {
		this.tables.add(table);
		this.curTable = table;
		return table;
	}
	
    public void addCondition(final String columnName, final String tableName, final BinaryOperator operator, final SQLExpr valueExpr, final DatabaseType databaseType, final List<Object> parameters) {
        Column column = createColumn(columnName, tableName);

        Comparable<?> value = evalExpression(databaseType, valueExpr, parameters);
        if (null != value) {
            addCondition(column, operator, Collections.<Comparable<?>>singletonList(value));
        }
    }

	public void addCondition(final SQLExpr expr, final BinaryOperator operator, final List<SQLExpr> valueExprList,
			final DatabaseType databaseType, final List<Object> parameters) {
		Optional<Column> column = getColumn(expr);
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

	private void addCondition(final Column column, final BinaryOperator operator, final List<Comparable<?>> values) {
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

	private Optional<Column> getColumn(final SQLExpr expr) {
		if (expr instanceof SQLPropertyExpr) {
			return Optional.fromNullable(getColumnWithQualifiedName((SQLPropertyExpr) expr));
		}
		if (expr instanceof SQLIdentifierExpr) {
			return Optional.fromNullable(getColumnWithoutAlias((SQLIdentifierExpr) expr));
		}
		return Optional.absent();
	}

	private Column getColumnWithQualifiedName(final SQLPropertyExpr expr) {
		Optional<Table> table = findTable(((SQLIdentifierExpr) expr.getOwner()).getName());
		return expr.getOwner() instanceof SQLIdentifierExpr && table.isPresent()
				? createColumn(expr.getName(), table.get().getName()) : null;
	}

	private Column getColumnWithoutAlias(final SQLIdentifierExpr expr) {
		return null != curTable ? createColumn(expr.getName(), curTable.getName()) : null;
	}

	private Column createColumn(final String columnName, final String tableName) {
		return new Column(SQLUtil.getExactlyValue(columnName), SQLUtil.getExactlyValue(tableName));
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

}
