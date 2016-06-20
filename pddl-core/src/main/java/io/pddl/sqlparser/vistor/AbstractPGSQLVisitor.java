package io.pddl.sqlparser.vistor;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.google.common.base.Optional;

import io.pddl.datasource.DatabaseType;
import io.pddl.sqlparser.SQLBuilder;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.SQLVisitor;
import io.pddl.sqlparser.bean.Table;
import io.pddl.sqlparser.bean.Condition.BinaryOperator;
import io.pddl.util.SQLUtil;

public abstract class AbstractPGSQLVisitor extends PGOutputVisitor implements SQLVisitor {
	
	protected Log logger = LogFactory.getLog(getClass());

	protected SQLParsedResult parseResult;

	protected AbstractPGSQLVisitor() {
		super(new SQLBuilder());
		setPrettyFormat(false);
		this.parseResult = new SQLParsedResult(getSQLBuilder());
	}

	@Override
	public final DatabaseType getDatabaseType() {
		return DatabaseType.PostgreSQL;
	}

	protected void setCurrentTable(final String currentTableName, final Optional<String> currentAlias) {
		Table table = new Table(SQLUtil.getExactlyValue(currentTableName),
				currentAlias.isPresent() ? Optional.of(SQLUtil.getExactlyValue(currentAlias.get())) : currentAlias);
		this.parseResult.addTable(table);
	}

	protected final SQLBuilder getSQLBuilder() {
		return (SQLBuilder) appender;
	}

	public final SQLParsedResult getSQLResult() {
		return this.parseResult;
	}

	@Override
	public final void printToken(final String token) {
		getSQLBuilder().appendToken(SQLUtil.getExactlyValue(token));
	}

	/**
	 * 父类使用<tt>@@</tt>代替<tt>?</tt>,此处直接输出参数占位符<tt>?</tt>
	 * 
	 * @param x
	 *            变量表达式
	 * @return false 终止遍历AST
	 */
	@Override
	public final boolean visit(final SQLVariantRefExpr x) {
		print(x.getName());
		return false;
	}

	@Override
	public final boolean visit(final SQLExprTableSource x) {
		return visit(x, parseResult.addTable(
				new Table(SQLUtil.getExactlyValue(x.getExpr().toString()), SQLUtil.getExactlyValue(x.getAlias()))));
	}

	private boolean visit(final SQLExprTableSource x, final Table table) {
		printToken(table.getName());
		if (table.getAlias().isPresent()) {
			print(' ');
			print(table.getAlias().get());
		}
		for (SQLHint each : x.getHints()) {
			print(' ');
			each.accept(this);
		}
		return false;
	}

	/**
	 * 将表名替换成占位符.
	 * 
	 * <p>
	 * 1. 如果二元表达式使用别名, 如: {@code FROM order o WHERE o.column_name = 't' },
	 * 则Column中的tableName为o.
	 * </p>
	 * 
	 * <p>
	 * 2. 如果二元表达式使用表名, 如: {@code FROM order WHERE order.column_name = 't' },
	 * 则Column中的tableName为order.
	 * </p>
	 * 
	 * @param x
	 *            SQL属性表达式
	 * @return true表示继续遍历AST, false表示终止遍历AST
	 */
	@Override
	// TODO SELECT [别名.xxx]的情况，目前都是替换成token，解析之后应该替换回去
	public final boolean visit(final SQLPropertyExpr x) {
		if (!(x.getParent() instanceof SQLBinaryOpExpr) && !(x.getParent() instanceof SQLSelectItem)) {
			return super.visit(x);
		}
		if (!(x.getOwner() instanceof SQLIdentifierExpr)) {
			return super.visit(x);
		}
		String tableOrAliasName = ((SQLIdentifierExpr) x.getOwner()).getLowerName();
		if (isBinaryOperateWithAlias(x, tableOrAliasName)) {
			return super.visit(x);
		}
		printToken(tableOrAliasName);
		print(".");
		print(x.getName());
		return false;
	}

	/**
	 * 判断SQL表达式是否为二元操作且带有别名.
	 * 
	 * @param x
	 *            待判断的SQL表达式
	 * @param tableOrAliasName
	 *            表名称或别名
	 * @return 是否为二元操作且带有别名
	 */
	public boolean isBinaryOperateWithAlias(final SQLPropertyExpr x, final String tableOrAliasName) {
		return x.getParent() instanceof SQLBinaryOpExpr
				&& parseResult.findTableFromAlias(SQLUtil.getExactlyValue(tableOrAliasName)).isPresent();
	}

	@Override
	public boolean visit(final SQLBinaryOpExpr x) {

		switch (x.getOperator()) {
		case BooleanOr:
			parseResult.setHasOrCondition(true);
			break;
		case Equality:
			parseResult.addCondition(x.getLeft(), BinaryOperator.EQUAL, Collections.singletonList(x.getRight()), getDatabaseType(), getParameters());
			parseResult.addCondition(x.getRight(), BinaryOperator.EQUAL, Collections.singletonList(x.getLeft()), getDatabaseType(), getParameters());
			break;
		default:
			break;
		}
		return super.visit(x);
	}

	@Override
	public boolean visit(final SQLInListExpr x) {
        if (!x.isNot()) {
        	parseResult.addCondition(x.getExpr(), BinaryOperator.IN, x.getTargetList(), getDatabaseType(), getParameters());
        }
		return super.visit(x);
	}

	@Override
	public boolean visit(final SQLBetweenExpr x) {
		parseResult.addCondition(x.getTestExpr(), BinaryOperator.BETWEEN, Arrays.asList(x.getBeginExpr(), x.getEndExpr()), getDatabaseType(), getParameters());
		return super.visit(x);
	}
}
