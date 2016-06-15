package io.pddl.sqlparser.vistor;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.AggregationColumn.AggregationType;
import io.pddl.sqlparser.bean.OrderColumn;
import io.pddl.sqlparser.bean.OrderColumn.OrderType;

public class PGSQLSelectVisitor extends AbstractPGSQLVisitor {
	
    //遍历表名
    @Override
    public boolean visit(final PGSelectQueryBlock x) {
        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableExpr = (SQLExprTableSource) x.getFrom();
            setCurrentTable(tableExpr.getExpr().toString(), Optional.fromNullable(tableExpr.getAlias()));
        }
        return super.visit(x);
    }
    //遍历选择项,不带公式的内容
    @Override
    public boolean visit(final SQLSelectItem x) {
    	if (Strings.isNullOrEmpty(x.getAlias())) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
            	parseResult.addSelectItem(((SQLIdentifierExpr) expr).getName());
            } else if (expr instanceof SQLPropertyExpr) {
            	parseResult.addSelectItem(((SQLPropertyExpr) expr).getName());
            } else if (expr instanceof SQLAllColumnExpr) {
            	parseResult.addSelectItem("*");
            }
        } else {
        	parseResult.addSelectItem(x.getAlias());
        }
        return super.visit(x);
    }
    //主要用于遍历MAX、MIN、COUNT、SUM和AVG操作
    @Override
    public boolean visit(final SQLAggregateExpr x) {
    	if (!(x.getParent() instanceof SQLSelectItem)) {
            return super.visit(x);
        }
    	try {
    		//如果不是AggregationType枚举中的操作，抛异常直接跳过不做处理
    		AggregationType aggregationType = AggregationType.valueOf(x.getMethodName().toUpperCase());
    		StringBuilder expression = new StringBuilder();
    		x.accept(new PGOutputVisitor(expression));
    		String alias= ((SQLSelectItem) x.getParent()).getAlias();
    		AggregationColumn aggregationColumn= new AggregationColumn(expression.toString(),alias,aggregationType);
    		if(logger.isInfoEnabled()){
    			logger.info("AggregationColumn: "+aggregationColumn);
    		}
    		parseResult.addAggregationColumn(aggregationColumn);
        } catch (final IllegalArgumentException ex) {
        }
    	return super.visit(x);
    }
    
    //在有表达式的SQLSelectItem后面加上COUNT(1) 即可，avg*count= sum
    @Override
    protected void printSelectList(final List<SQLSelectItem> selectList) {
        super.printSelectList(selectList);
        for(SQLSelectItem item: selectList){
        	if(item.getExpr() instanceof SQLAggregateExpr){
        		AggregationType type= AggregationType.valueOf(((SQLAggregateExpr)item.getExpr()).getMethodName().toUpperCase());
        		if(AggregationType.AVG.equals(type)){
        			print(", COUNT(1) AS auto_gen_count_key");
        			if(logger.isInfoEnabled()){
            			logger.info("auto create count(1) AS auto_gen_count_key column");
            		}
        			return;
        		}
        	}
        }
    }
    
    //遍历orderby内容
    @Override
    public boolean visit(final SQLOrderBy x) {
    	for (SQLSelectOrderByItem each : x.getItems()) {
            SQLExpr expr = each.getExpr();
            OrderType orderType= "DESC".equals(each.getType())? OrderType.DESC : OrderType.ASC;
            if (expr instanceof SQLIntegerExpr) {
            	int index= ((SQLIntegerExpr)expr).getNumber().intValue();
            	if(index > parseResult.getSelectItems().size()){
            		logger.warn("order index ["+index+"] is outboundary of columns: "+parseResult.getSelectItems());
            		continue;
            	}
            	parseResult.addOrderColumn(new OrderColumn(index,parseResult.getSelectItems().get(index-1),orderType));
            } else if (expr instanceof SQLIdentifierExpr) {
            	String name= ((SQLIdentifierExpr)expr).getName();
            	int index= parseResult.getSelectItems().indexOf(name);
            	if(index!= -1){
            		parseResult.addOrderColumn(new OrderColumn(++index,name,orderType));
            		if(logger.isInfoEnabled()){
            			logger.info("order name ["+name+"]'s index is: "+index);
            		}
            	}
            	else{
            		logger.warn("cannot found name ["+name+"] in columns: "+parseResult.getSelectItems());
            	}
            } else if (expr instanceof SQLPropertyExpr) {
                String name= ((SQLPropertyExpr)expr).getName();
            	int index= parseResult.getSelectItems().indexOf(name);
            	if(index!= -1){
            		parseResult.addOrderColumn(new OrderColumn(++index,name,orderType));
            		if(logger.isInfoEnabled()){
            			logger.info("order name ["+name+"]'s index is: "+index);
            		}
            	}
            	else{
            		logger.warn("cannot found name ["+name+"] in columns: "+parseResult.getSelectItems());
            	}
            }
        }
        return super.visit(x);
    }

}
