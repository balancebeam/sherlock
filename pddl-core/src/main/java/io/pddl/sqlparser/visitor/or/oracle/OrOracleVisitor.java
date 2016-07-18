package io.pddl.sqlparser.visitor.or.oracle;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.wall.spi.WallVisitorUtils;
import com.google.common.base.Optional;

import io.pddl.sqlparser.SQLVisitor;
import io.pddl.sqlparser.visitor.or.AbstractOrASTNode;
import io.pddl.sqlparser.visitor.or.CompositeOrASTNode;
import io.pddl.sqlparser.visitor.or.OrVisitor;
import io.pddl.sqlparser.visitor.or.SimpleOrASTNode;
import io.pddl.sqlparser.visitor.oracle.AbstractOracleVisitor;

public class OrOracleVisitor extends AbstractOracleVisitor implements OrVisitor {
    
    private AbstractOrASTNode orASTNode;
    
    public OrOracleVisitor(final SQLASTOutputVisitor dependencyVisitor) {
        setParameters(dependencyVisitor.getParameters());
        SQLVisitor visitor = (SQLVisitor) dependencyVisitor;
        if (null != visitor.getSQLResult().getCurTable()) {
        	parseResult.setCurTable(visitor.getSQLResult().getCurTable());
        }
        parseResult.getTables().addAll(visitor.getSQLResult().getTables());
    }
    
    /**
     * 进行OR表达式的访问.
     *
     * @param sqlObject SQL对象
     * @return OR访问节点
     */
    public Optional<AbstractOrASTNode> visitHandle(final SQLObject sqlObject) {
        reset();
        sqlObject.accept(this);
        postVisitHandle();
        return Optional.fromNullable(orASTNode);
    }
    
    private void reset() {
        orASTNode = null;
        parseResult.getCurCondition().clear();
        parseResult.setHasOrCondition(false);
    }
    
    private void postVisitHandle() {
        if (null == orASTNode) {
            return;
        }
        if (!parseResult.getCurCondition().isEmpty()) {
            CompositeOrASTNode existingOutConditionOrASTNode = new CompositeOrASTNode();
            existingOutConditionOrASTNode.addSubNode(orASTNode);
            existingOutConditionOrASTNode.addOutConditions(parseResult.getCurCondition());
            orASTNode = existingOutConditionOrASTNode;
        }
        orASTNode.createOrASTAsRootNode();
    }
    
    /**
     * 逻辑OR访问器, 每次只解析一层OR条件.
     * 
     * @param x 二元表达式
     * @return false 停止访问AST
     */
    @Override
    public boolean visit(final SQLBinaryOpExpr x) {
        if (!SQLBinaryOperator.BooleanOr.equals(x.getOperator())) {
            return super.visit(x);
        }
        if (Boolean.TRUE.equals(WallVisitorUtils.getValue(x))) {
            return false;
        }
        if (orASTNode == null) {
            orASTNode = new SimpleOrASTNode(x, new OrOracleVisitor(this));
        } else {
            CompositeOrASTNode existingOutConditionOrASTNode = new CompositeOrASTNode();
            existingOutConditionOrASTNode.addSubNode(orASTNode);
            existingOutConditionOrASTNode.addSubNode(new SimpleOrASTNode(x, new OrOracleVisitor(this)));
            orASTNode = existingOutConditionOrASTNode;
        }
        return false;
    }
}
