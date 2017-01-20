package io.anyway.sherlock.sqlparser.visitor.or;

import io.anyway.sherlock.sqlparser.SQLVisitor;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.google.common.base.Optional;

public interface OrVisitor extends SQLVisitor {
	public Optional<AbstractOrASTNode> visitHandle(final SQLObject sqlObject);

	public boolean visit(final SQLBinaryOpExpr x);
}
