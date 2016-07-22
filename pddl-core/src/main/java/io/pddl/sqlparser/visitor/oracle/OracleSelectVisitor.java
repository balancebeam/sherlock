package io.pddl.sqlparser.visitor.oracle;

import java.util.LinkedList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.google.common.base.Optional;

import io.pddl.exception.SQLParserException;
import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.AggregationColumn.AggregationType;
import io.pddl.sqlparser.bean.GroupColumn;
import io.pddl.sqlparser.bean.OrderColumn;
import io.pddl.sqlparser.bean.OrderColumn.OrderType;

/**
 * Oralce查询访问收集器，主要收集表名、条件项、列元数据、distinct、聚合表达式、groupby、orderby和limit等内容
 * @author liufeng
 *
 */
public class OracleSelectVisitor extends AbstractOracleVisitor {
	
	final private static String AUTO_GEN_COL= "auto_gen_col_";
	
	final private static String AUTO_GEN_COL_COUNT= AUTO_GEN_COL + "count";
	
	private int selectLayer= 0;
	
	private boolean finishCollectMetadata= false;
	
	private boolean attachCountExpression= false;
	
	private List<String> missOrderbyColumns;
	
    @Override
    public boolean visit(final OracleSelectQueryBlock x) {
    	selectLayer++;
        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableExpr = (SQLExprTableSource) x.getFrom();
            setCurrentTable(tableExpr.getExpr().toString(), Optional.fromNullable(tableExpr.getAlias()));
        }
        if(isEnableCollectMetadata()){
        	if(SQLSetQuantifier.DISTINCT==x.getDistionOption()){
        		parseResult.markDistinct();
        	}
        }
        return super.visit(x);
    }
    
    @Override
    public void endVisit(final OracleSelectQueryBlock x) {
    	if(isMasterSelect()){
	    	if(!CollectionUtils.isEmpty(missOrderbyColumns)){
	    		String orderby_columns="";
	    		for(String columnName: missOrderbyColumns){
	    			orderby_columns+= ", "+ columnName;
	    		}
	    		parseResult.getSqlBuilder().buildSQL("select_missing_columns", orderby_columns);
	    	}

	    	if(parseResult.getLimit()== null){
	    		//print(" limit 9");
	    	}
    	}
    	
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
    

    @Override
    protected void printSelectList(List<SQLSelectItem> selectList) {
        super.printSelectList(selectList);
        if(!isMasterSelect()){
        	return;
        }
        int columnIndex= 0;
        for(SQLSelectItem each: selectList){
        	columnIndex++;
        	String alias= each.getAlias();
        	SQLExpr expr= each.getExpr();
        	if(expr instanceof SQLAggregateExpr){
        		SQLAggregateExpr x= (SQLAggregateExpr)expr;
        		StringBuilder expression = new StringBuilder();
        		x.accept(new OracleOutputVisitor(expression));
        		try{

	        		AggregationType aggregationType = AggregationType.valueOf(x.getMethodName().toUpperCase());
	        		if(isEnableCollectMetadata()){
		        		AggregationColumn aggregationColumn= new AggregationColumn(expression.toString(),columnIndex,aggregationType);
		        		parseResult.addAggregationColumn(aggregationColumn);
		        		if(logger.isInfoEnabled()){
		        			logger.info("AggregationColumn: "+aggregationColumn);
		        		}
	        		}

	        		if(AggregationType.AVG.equals(aggregationType) && !attachCountExpression){
	        			print(", COUNT(1) AS " + AUTO_GEN_COL_COUNT);
	        			attachCountExpression= true;
	        			if(logger.isInfoEnabled()){
	            			logger.info("auto create count(1) AS "+AUTO_GEN_COL_COUNT);
	            		}
	        		}
        		}catch(IllegalArgumentException ex){
        			if(logger.isInfoEnabled()){
            			logger.info(expression.toString()+" expression not in (MAX銆丮IN銆丆OUNT銆丼UM and AVG)");
            		}
        		}
        	}
        	

        	if(isEnableCollectMetadata()){
	        	if(!StringUtils.isEmpty(alias)){
	        		parseResult.addMetadataColumn(alias);
	        		continue;
	        	}

	        	if(expr instanceof SQLIdentifierExpr){
	        		parseResult.addMetadataColumn(((SQLIdentifierExpr)expr).getName());
	        	}
	        	else if(expr instanceof SQLPropertyExpr){
	        		parseResult.addMetadataColumn(((SQLPropertyExpr)expr).getName());
	        	}
	        	else if(expr instanceof SQLAllColumnExpr){
	        		throw new SQLParserException("not support select * for sql: "+sql+",please enumerate every column");
	        	}
	        	else{
	        		String columnName= AUTO_GEN_COL + columnIndex;
	        		parseResult.addMetadataColumn(columnName);
	        		StringBuilder expression = new StringBuilder();
	        		expr.accept(new OracleOutputVisitor(expression));
	        		logger.warn(expression.toString()+" miss alias, will use temporary column name: "+columnName);
	        	}
        	}
        }
        
    	parseResult.getSqlBuilder().appendToken("select_missing_columns", false);
    }
    
    @Override
    public boolean visit(SQLSelectGroupByClause x){
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
    

    @Override
    public boolean visit(SQLOrderBy x) {
    	if(!isEnableCollectMetadata()){
    		return super.visit(x);
    	}
    	for (SQLSelectOrderByItem each : x.getItems()) {
            SQLExpr expr = each.getExpr();
            OrderType orderType= each.getType()== null? OrderType.ASC: ("DESC".equalsIgnoreCase(each.getType().toString())? OrderType.DESC : OrderType.ASC);
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
            String owner= null;
            if (expr instanceof SQLIdentifierExpr) {
            	columnName= ((SQLIdentifierExpr)expr).getName();
            }
            else if (expr instanceof SQLPropertyExpr) {
                columnName= ((SQLPropertyExpr)expr).getName();
                SQLExpr exp= ((SQLPropertyExpr)expr).getOwner();
                if(exp!=null){
                	owner= exp.toString();
                }
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
            		if(CollectionUtils.isEmpty(parseResult.getGroupColumns())){
	            		index= parseResult.getMetadataColumns().size() + (attachCountExpression?1:0);
	            		if(StringUtils.isEmpty(missOrderbyColumns)){
	            			missOrderbyColumns= new LinkedList<String>();
	            		}
	            		if(!missOrderbyColumns.contains(columnName)){
	            			missOrderbyColumns.add((owner==null?"":owner+".")+columnName);
	            			index= missOrderbyColumns.size() + index;
	            			parseResult.addOrderColumn(new OrderColumn(columnName,orderType,index));
	            			if(logger.isInfoEnabled()){
	                			logger.info("will append missing orderby column ["+columnName+"] index is: "+index);
	                		}
	            		}
	            		else{
	            			logger.warn("duplicated orderby column ["+columnName+"] "+orderType +",it doesnt work");
	            		}
            		}
            	}
            }
        }
        return super.visit(x);
    }

}
