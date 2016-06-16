package io.pddl.sqlparser.vistor;

import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.google.common.base.Optional;

import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.AggregationColumn.AggregationType;
import io.pddl.sqlparser.bean.OrderColumn;
import io.pddl.sqlparser.bean.OrderColumn.OrderType;

public class PGSQLSelectVisitor extends AbstractPGSQLVisitor {
	
	private int selectLayer= 0;
	
	private boolean finishSelectItem= false;
	
    //遍历表名
    @Override
    public boolean visit(final PGSelectQueryBlock x) {
        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableExpr = (SQLExprTableSource) x.getFrom();
            setCurrentTable(tableExpr.getExpr().toString(), Optional.fromNullable(tableExpr.getAlias()));
        }
        return super.visit(x);
    }
    
    //访问SELECT执行操作
    @Override
    public boolean visit(SQLSelect x) {
    	selectLayer++;
    	return super.visit(x);
    }
   
    //访问SELECT结束后操作
    @Override
    public void endVisit(SQLSelect x) {
    	if(isFirstLayerSelect()){
    		finishSelectItem= true;
    	}
    	selectLayer--;
    }
    
    /*
     * 主要屏蔽嵌套子查询和UNIO后边查询的东西
     * 
     * 场景一：select a ,(select b from ...) from ... where xx= (select xx from ...); 不处理嵌套子查询
     * 场景二：(select xxx from yyy) UNION [ALL] (select xxx from yyy2); 不处理UNION 后边的查询
     */
    private boolean isFirstLayerSelect(){
    	return !finishSelectItem && selectLayer == 1;
    }
    
    /*
     * 只遍历第一级SELECT选择项，收集Metadata信息，同时收集MAX、MIN、COUNT、SUM和AVG操作；建议子查询和表达式使用alias别名
     */
    @Override
    protected void printSelectList(List<SQLSelectItem> selectList) {
        super.printSelectList(selectList);
        //如果是嵌套子查询直接滤过
        if(!isFirstLayerSelect()){
        	return;
        }
        boolean attachCountExpression= false;
        int columnIndex= 0;
        //遍历第一级SELECT选择项
        for(SQLSelectItem each: selectList){
        	columnIndex++;
        	String alias= each.getAlias();
        	SQLExpr expr= each.getExpr();
        	//如果是表达式
        	if(expr instanceof SQLAggregateExpr){
        		SQLAggregateExpr x= (SQLAggregateExpr)expr;
        		StringBuilder expression = new StringBuilder();
        		x.accept(new PGOutputVisitor(expression));
        		try{
        			//如果不是AggregationType枚举中的内容，抛异常直接跳过不做处理
	        		AggregationType aggregationType = AggregationType.valueOf(x.getMethodName().toUpperCase());
	        		AggregationColumn aggregationColumn= new AggregationColumn(expression.toString(),columnIndex,aggregationType);
	        		parseResult.addAggregationColumn(aggregationColumn);
	        		if(logger.isInfoEnabled()){
	        			logger.info("AggregationColumn: "+aggregationColumn);
	        		}
	        		//在有表达式的SQLSelectItem后面加上只追加一个COUNT(1) 即可，然后通过(avg1*count1+avg2*count2)/(count1+count2)求最终平均值
	        		//原始SQL：select avg(salary) as avg_salary, (select avg(bonus) from user) from user  
	        	    //转换SQL：select avg(salary) as avg_salary, (select avg(bonus) from user), count(1) as auto_gen_cont_key from user
	        		if(AggregationType.AVG.equals(aggregationType) && !attachCountExpression){
	        			print(", COUNT(1) AS auto_gen_count_key");
	        			attachCountExpression= true;
	        			if(logger.isInfoEnabled()){
	            			logger.info("auto create count(1) AS auto_gen_count_key column");
	            		}
	        		}
        		}catch(IllegalArgumentException ex){
        			if(logger.isInfoEnabled()){
            			logger.info(expression.toString()+" expression not in (MAX、MIN、COUNT、SUM and AVG)");
            		}
        		}
        	}
        	//如果别名不为空，则存储别名
        	if(!StringUtils.isEmpty(alias)){
        		parseResult.addMetadataName(alias);
        		continue;
        	}
        	//如果是其他列名
        	if(expr instanceof SQLIdentifierExpr){
        		parseResult.addMetadataName(((SQLIdentifierExpr)expr).getName());
        	}
        	else if(expr instanceof SQLPropertyExpr){
        		parseResult.addMetadataName(((SQLPropertyExpr)expr).getName());
        	}
        	else if(expr instanceof SQLAllColumnExpr){
        		parseResult.addMetadataName("*");
        	}
        	else{
        		//可能还是子查询SQLQueryExpr
        		String columnName= "auto_gen_col_"+columnIndex;
        		parseResult.addMetadataName(columnName);
        		StringBuilder expression = new StringBuilder();
        		expr.accept(new PGOutputVisitor(expression));
        		logger.warn(expression.toString()+" miss alias, will use "+columnName+" temporarily");
        	}
        }
    }
    
    /*
     * 遍历orderby内容
     */
    @Override
    public boolean visit(SQLOrderBy x) {
    	//如果嵌套子查询中的order by直接跳过
    	if(!isFirstLayerSelect()){
    		return visit(x);
    	}
    	for (SQLSelectOrderByItem each : x.getItems()) {
            SQLExpr expr = each.getExpr();
            OrderType orderType= "DESC".equals(each.getType())? OrderType.DESC : OrderType.ASC;
            if (expr instanceof SQLIntegerExpr) {
            	int index= ((SQLIntegerExpr)expr).getNumber().intValue();
            	if(index > parseResult.getMetadataNames().size()){
            		logger.warn("order index ["+index+"] is outboundary of columns: "+parseResult.getMetadataNames());
            		continue;
            	}
            	parseResult.addOrderColumn(new OrderColumn(index,parseResult.getMetadataNames().get(index-1),orderType));
            } else if (expr instanceof SQLIdentifierExpr) {
            	String name= ((SQLIdentifierExpr)expr).getName();
            	int index= parseResult.getMetadataNames().indexOf(name);
            	if(index!= -1){
            		parseResult.addOrderColumn(new OrderColumn(++index,name,orderType));
            		if(logger.isInfoEnabled()){
            			logger.info("order name ["+name+"]'s index is: "+index);
            		}
            	}
            	else{
            		logger.warn("cannot found name ["+name+"] in columns: "+parseResult.getMetadataNames());
            	}
            } else if (expr instanceof SQLPropertyExpr) {
                String name= ((SQLPropertyExpr)expr).getName();
            	int index= parseResult.getMetadataNames().indexOf(name);
            	if(index!= -1){
            		parseResult.addOrderColumn(new OrderColumn(++index,name,orderType));
            		if(logger.isInfoEnabled()){
            			logger.info("order name ["+name+"]'s index is: "+index);
            		}
            	}
            	else{
            		logger.warn("cannot found name ["+name+"] in columns: "+parseResult.getMetadataNames());
            	}
            }
        }
        return super.visit(x);
    }
    
    /*
     * TODO
     * Druid 不支持解析Groupby缺少PGSelectGroupByExpr，此版本暂时不支持groupby
     */
//    @Override
//    public boolean visit(PGSelectGroupByExpr x) {
//        return super.visit(x);
//    }

}
