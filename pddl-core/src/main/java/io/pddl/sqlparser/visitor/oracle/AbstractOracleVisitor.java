package io.pddl.sqlparser.visitor.oracle;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.google.common.base.Optional;

import io.pddl.datasource.DatabaseType;
import io.pddl.merger.Limit;
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

	public boolean isBinaryOperateWithAlias(final SQLPropertyExpr x, final String tableOrAliasName) {
		return x.getParent() instanceof SQLBinaryOpExpr
				&& parseResult.findTableFromAlias(SQLUtil.getExactlyValue(tableOrAliasName)).isPresent();
	}

	@Override
	public boolean visit(final SQLBinaryOpExpr x) {
		String sqlExpr = SQLUtil.getExactlyValue(x.getLeft().toString());
		if(sqlExpr.toLowerCase().equals("rownum") || parseResult.getRownumAlias().contains(sqlExpr)) {
			SQLExpr intExpr = new SQLIntegerExpr(0); 
			switch(x.getOperator()) {
			case LessThan:
				parseResult.getLimit().setUpperBound(Integer.parseInt(x.getRight().toString()));
				break;
			case LessThanOrEqual:
				parseResult.getLimit().setUpperBound(Integer.parseInt(x.getRight().toString()) + 1);
				break;
			case GreaterThan:
				parseResult.getLimit().setOffset((Integer.parseInt(x.getRight().toString()) + 1));	
				x.setRight(intExpr);
				break;
			case GreaterThanOrEqual:
				parseResult.getLimit().setOffset(Integer.parseInt(x.getRight().toString()));
				x.setRight(intExpr);
				break;
			case Equality:
				parseResult.getLimit().setUpperBound(Integer.parseInt(x.getRight().toString())+1);
				parseResult.getLimit().setOffset(Integer.parseInt(x.getRight().toString()));
				x.setOperator(SQLBinaryOperator.LessThanOrEqual);
			default:
				break;
			}
        }

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
	public boolean visit(SQLSelectItem x) {
        if (x.isConnectByRoot()) {
            print0(ucase ? "CONNECT_BY_ROOT " : "connect_by_root ");
        }

        x.getExpr().accept(this);
        
        String alias = x.getAlias();
        if (alias != null && alias.length() > 0) {
            print0(ucase ? " AS " : " as ");
            if (alias.indexOf(' ') == -1 || alias.charAt(0) == '"' || alias.charAt(0) == '\'') {
                print0(alias);
            } else {
                print('"');
                print0(alias);
                print('"');
            }
            
        }
        
        String sqlExpr = SQLUtil.getExactlyValue(x.getExpr().toString());
        if(sqlExpr.toLowerCase().equals("rownum")) {
        	Limit limit =  new Limit(-1,-1);
        	parseResult.setLimit(limit);
        	if (alias != null && alias.length() > 0) {
        		parseResult.getRownumAlias().add(alias);
        	}
        }
        
        return false;
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
