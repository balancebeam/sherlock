package io.pddl.sqlparser.visitor.or;

import io.pddl.sqlparser.SQLVisitor;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.google.common.base.Optional;

public interface OrVisitor extends SQLVisitor {
	public Optional<AbstractOrASTNode> visitHandle(final SQLObject sqlObject);

	public boolean visit(final SQLBinaryOpExpr x);
}
