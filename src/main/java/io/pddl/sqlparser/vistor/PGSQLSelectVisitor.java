package io.pddl.sqlparser.vistor;

import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.PGLimit;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.google.common.base.Optional;

import io.pddl.merger.Limit;
import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.AggregationColumn.AggregationType;
import io.pddl.sqlparser.bean.GroupColumn;
import io.pddl.sqlparser.bean.OrderColumn;
import io.pddl.sqlparser.bean.OrderColumn.OrderType;

public class PGSQLSelectVisitor extends AbstractPGSQLVisitor {
	
	private int selectLayer= 0;
	
	private boolean tryUnion= false;
	
	private boolean finishCollectMetadata= false;
	
    //遍历表名
    @Override
    public boolean visit(final PGSelectQueryBlock x) {
        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableExpr = (SQLExprTableSource) x.getFrom();
            setCurrentTable(tableExpr.getExpr().toString(), Optional.fromNullable(tableExpr.getAlias()));
        }
        //处理distinct
        if(isEnableCollectMetadata()){
        	if(SQLSetQuantifier.DISTINCT==x.getDistionOption()){
        		parseResult.markDistinct();
        	}
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
    	if(isEnableCollectMetadata()){
    		finishCollectMetadata= true;
    	}
    	selectLayer--;
    }
    
    /*
     * 主要屏蔽嵌套子查询和UNIO后边查询的东西
     * 
     * 场景一：select a ,(select b from ...) from ... where xx= (select xx from ...); 不处理嵌套子查询
     * 场景二：(select xxx from yyy) UNION [ALL] (select xxx from yyy2); 不处理UNION 后边的查询
     */
    private boolean isEnableCollectMetadata(){
    	return !finishCollectMetadata && selectLayer == 1;
    }
    
    private boolean isMasterSelect(){
    	return selectLayer == 1;
    }
    
    /*
     * 只遍历第一级SELECT选择项，收集Metadata信息，同时收集MAX、MIN、COUNT、SUM和AVG操作；建议子查询和表达式使用alias别名
     */
    @Override
    protected void printSelectList(List<SQLSelectItem> selectList) {
        super.printSelectList(selectList);
        //如果是嵌套子查询直接滤过
        if(!isMasterSelect()){
        	return;
        }
        //防止union后边再次触发收集操作，使用visit(SQLUnionQuery x)最先执行导致无效
        if(tryUnion){
        	finishCollectMetadata= true;
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
	        		//解析过一遍就不在解析，此段逻辑主要处理如下场景
	        		//(select avg(salary) from emp where userid<500) union (select avg(salary) from emp where userid>100)
	        		//解析完第一个select的元数据就不在解析，但是对于union后面的select语句，avg聚合字段也要追加 count(1)列
	        		if(isEnableCollectMetadata()){
		        		AggregationColumn aggregationColumn= new AggregationColumn(expression.toString(),columnIndex,aggregationType);
		        		parseResult.addAggregationColumn(aggregationColumn);
		        		if(logger.isInfoEnabled()){
		        			logger.info("AggregationColumn: "+aggregationColumn);
		        		}
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
        	//可以收集列元数据信息
        	if(isEnableCollectMetadata()){
	        	//如果别名不为空，则存储别名
	        	if(!StringUtils.isEmpty(alias)){
	        		parseResult.addMetadataColumn(alias);
	        		continue;
	        	}
	        	//如果是其他列名
	        	if(expr instanceof SQLIdentifierExpr){
	        		parseResult.addMetadataColumn(((SQLIdentifierExpr)expr).getName());
	        	}
	        	else if(expr instanceof SQLPropertyExpr){
	        		parseResult.addMetadataColumn(((SQLPropertyExpr)expr).getName());
	        	}
	        	else if(expr instanceof SQLAllColumnExpr){
	        		parseResult.addMetadataColumn("*");
	        	}
	        	else{
	        		//可能还是子查询SQLQueryExpr
	        		String columnName= "auto_gen_col_"+columnIndex;
	        		parseResult.addMetadataColumn(columnName);
	        		StringBuilder expression = new StringBuilder();
	        		expr.accept(new PGOutputVisitor(expression));
	        		logger.warn(expression.toString()+" miss alias, will use "+columnName+" temporarily");
	        	}
        	}
        }
        //解析完毕立即设置防止union后边的触发收集操作
        tryUnion= true;
    }
    
    //解析Groupby节点
    @Override
    public boolean visit(SQLSelectGroupByClause x){
    	//如果嵌套子查询或第一个SQL解析完毕
    	if(!isEnableCollectMetadata()){
    		return super.visit(x); 
    	}
    	for(SQLExpr expr:x.getItems()){
    		String columnName= null;
    		if(expr instanceof SQLPropertyExpr) {
    			columnName= ((SQLPropertyExpr)expr).getName();
    		} else if (expr instanceof SQLIdentifierExpr) {
    			columnName = ((SQLIdentifierExpr)expr).getName();
    		}
    		if(!StringUtils.isEmpty(columnName)){
				int index= parseResult.getMetadataColumns().indexOf(columnName);
				if(index!= -1){
					parseResult.addGroupColumn(new GroupColumn(columnName,++index));
					if(logger.isInfoEnabled()){
	        			logger.info("group column ["+columnName+"] index is: "+index);
	        		}
				}
				else{
					logger.warn("cannot found group column ["+columnName+"] in metadatacolumns: "+parseResult.getMetadataColumns());
				}
    		}
    	}
    	return super.visit(x);
    }
    
    /*
     * 遍历orderby内容
     */
    @Override
    public boolean visit(SQLOrderBy x) {
    	//如果嵌套子查询或第一个SQL解析完毕
    	if(!isEnableCollectMetadata()){
    		return visit(x);
    	}
    	for (SQLSelectOrderByItem each : x.getItems()) {
            SQLExpr expr = each.getExpr();
            OrderType orderType= "DESC".equals(each.getType())? OrderType.DESC : OrderType.ASC;
            if (expr instanceof SQLIntegerExpr) {
            	int index= ((SQLIntegerExpr)expr).getNumber().intValue();
            	if(index > parseResult.getMetadataColumns().size()){
            		logger.warn("order index ["+index+"] is outboundary of metadatacolumns: "+parseResult.getMetadataColumns());
            	}
            	else{
            		parseResult.addOrderColumn(new OrderColumn(parseResult.getMetadataColumns().get(index-1),orderType,index));
            	}
            	continue;
            } 
            String columnName= null;
            if (expr instanceof SQLIdentifierExpr) {
            	columnName= ((SQLIdentifierExpr)expr).getName();
            }
            else if (expr instanceof SQLPropertyExpr) {
                columnName= ((SQLPropertyExpr)expr).getName();
            }
            if(!StringUtils.isEmpty(columnName)){
            	int index= parseResult.getMetadataColumns().indexOf(columnName);
            	if(index!= -1){
            		parseResult.addOrderColumn(new OrderColumn(columnName,orderType,++index));
            		if(logger.isInfoEnabled()){
            			logger.info("order column ["+columnName+"] index is: "+index);
            		}
            	}
            	else{
            		logger.warn("cannot found order column ["+columnName+"] in metadatacolumns: "+parseResult.getMetadataColumns());
            	}
            }
        }
        return super.visit(x);
    }
    
    //解析Limit节点
    @Override
    public boolean visit(PGLimit x) {
    	//如果嵌套子查询或第一个SQL解析完毕
    	if(!isEnableCollectMetadata()){
    		return super.visit(x); 
    	}
    	//第一次解析
        print("LIMIT ");
        int offset = 0;
        if (null != x.getOffset()) {
            if (x.getOffset() instanceof SQLNumericLiteralExpr) {
                offset = ((SQLNumericLiteralExpr) x.getOffset()).getNumber().intValue();
                print("0, ");
            } else {
                offset = ((Number) getParameters().get(((SQLVariantRefExpr) x.getOffset()).getIndex())).intValue();
                getParameters().set(((SQLVariantRefExpr) x.getOffset()).getIndex(), 0);
                print("?, ");
            }
        }
        int rowCount;
        if (x.getRowCount() instanceof SQLNumericLiteralExpr) {
            rowCount = ((SQLNumericLiteralExpr) x.getRowCount()).getNumber().intValue();
            print(rowCount + offset);
        } else {
            rowCount = ((Number) getParameters().get(((SQLVariantRefExpr) x.getRowCount()).getIndex())).intValue();
            getParameters().set(((SQLVariantRefExpr) x.getRowCount()).getIndex(), rowCount + offset);
            print("?");
        }
        parseResult.setLimit(new Limit(offset, rowCount));
        if(logger.isInfoEnabled()){
			logger.info("Limit [offset: "+offset+",rowCount: "+rowCount+"]");
		}
        return false;
    }

}
