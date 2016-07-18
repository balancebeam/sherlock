package io.pddl.sqlparser.visitor.oracle;

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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.google.common.base.Optional;

import io.pddl.datasource.DatabaseType;
import io.pddl.sqlparser.SQLAware;
import io.pddl.sqlparser.SQLBuilder;
import io.pddl.sqlparser.SQLParsedResult;
import io.pddl.sqlparser.SQLVisitor;
import io.pddl.sqlparser.bean.Condition.BinaryOperator;
import io.pddl.sqlparser.bean.Table;
import io.pddl.util.SQLUtil;

public abstract class AbstractOracleVisitor extends OracleOutputVisitor implements SQLVisitor,SQLAware {
	
	protected Log logger = LogFactory.getLog(getClass());

	protected SQLParsedResult parseResult;
	
	protected String sql;

	protected AbstractOracleVisitor() {
		super(new SQLBuilder());
		setPrettyFormat(false);
		this.parseResult = new SQLParsedResult(getSQLBuilder());
	}

	@Override
	public final DatabaseType getDatabaseType() {
		return DatabaseType.Oracle;
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
	 * 鐖剁被浣跨敤<tt>@@</tt>浠ｆ浛<tt>?</tt>,姝ゅ鐩存帴杈撳嚭鍙傛暟鍗犱綅绗�tt>?</tt>
	 * 
	 * @param x
	 *            鍙橀噺琛ㄨ揪寮�
	 * @return false 缁堟閬嶅巻AST
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
	
	public boolean visit(OracleSelectTableReference x) {
		String tableName = SQLUtil.getExactlyValue(x.getExpr().toString());
		parseResult.addTable(new Table(tableName, SQLUtil.getExactlyValue(x.getAlias())));
		 if (x.isOnly()) {
	            print0(ucase ? "ONLY (" : "only (");
	            printToken(tableName);

	            if (x.getPartition() != null) {
	                print(' ');
	                x.getPartition().accept(this);
	            }

	            print(')');
	        } else {
	        	printToken(SQLUtil.getExactlyValue(x.getExpr().toString()));

	            if (x.getPartition() != null) {
	                print(' ');
	                x.getPartition().accept(this);
	            }
	        }

	        if (x.getHints().size() > 0) {
	        	for (SQLHint each : x.getHints()) {
	    			print(' ');
	    			each.accept(this);
	    		}
	        }

	        if (x.getSampleClause() != null) {
	            print(' ');
	            x.getSampleClause().accept(this);
	        }

	        if (x.getPivot() != null) {
	            println();
	            x.getPivot().accept(this);
	        }

	        if (x.getFlashback() != null) {
	            println();
	            x.getFlashback().accept(this);
	        }

	        printAlias(x.getAlias());

	        return false;
	}
	/**
	 * 灏嗚〃鍚嶆浛鎹㈡垚鍗犱綅绗�.
	 * 
	 * <p>
	 * 1. 濡傛灉浜屽厓琛ㄨ揪寮忎娇鐢ㄥ埆鍚�, 濡� {@code FROM order o WHERE o.column_name = 't' },
	 * 鍒機olumn涓殑tableName涓簅.
	 * </p>
	 * 
	 * <p>
	 * 2. 濡傛灉浜屽厓琛ㄨ揪寮忎娇鐢ㄨ〃鍚�, 濡� {@code FROM order WHERE order.column_name = 't' },
	 * 鍒機olumn涓殑tableName涓簅rder.
	 * </p>
	 * 
	 * @param x
	 *            SQL灞炴�ц〃杈惧紡
	 * @return true琛ㄧず缁х画閬嶅巻AST, false琛ㄧず缁堟閬嶅巻AST
	 */
	@Override
	// TODO SELECT [鍒悕.xxx]鐨勬儏鍐碉紝鐩墠閮芥槸鏇挎崲鎴恡oken锛岃В鏋愪箣鍚庡簲璇ユ浛鎹㈠洖鍘�
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
	 * 鍒ゆ柇SQL琛ㄨ揪寮忔槸鍚︿负浜屽厓鎿嶄綔涓斿甫鏈夊埆鍚�.
	 * 
	 * @param x
	 *            寰呭垽鏂殑SQL琛ㄨ揪寮�
	 * @param tableOrAliasName
	 *            琛ㄥ悕绉版垨鍒悕
	 * @return 鏄惁涓轰簩鍏冩搷浣滀笖甯︽湁鍒悕
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
	
	@Override
	public void setSQL(String sql){
		this.sql= sql;
	}
}
