package io.pddl.sqlparser.visitor.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.google.common.base.Optional;
import io.pddl.exception.SQLParserException;
import io.pddl.merger.Limit;
import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.AggregationColumn.AggregationType;
import io.pddl.sqlparser.bean.GroupColumn;
import io.pddl.sqlparser.bean.OrderColumn;
import io.pddl.sqlparser.bean.OrderColumn.OrderType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * PostgreSQL查询访问收集器，主要收集表名、条件项、列元数据、distinct、聚合表达式、groupby、orderby和limit等内容
 * @author yangzz
 *
 */
public class MySQLSelectVisitor extends AbstractMySQLVisitor {
	
	final private static String AUTO_GEN_COL= "auto_gen_col_";
	
	final private static String AUTO_GEN_COL_COUNT= AUTO_GEN_COL + "count";
	
	private int selectLayer= 0;
	
	private boolean finishCollectMetadata= false;
	
	private boolean attachCountExpression= false;
	
	private List<String> missOrderbyColumns;
	
    //遍历表名
    @Override
    public boolean visit(final MySqlSelectQueryBlock x) {
    	selectLayer++;
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
    
    @Override
    public void endVisit(final MySqlSelectQueryBlock x) {
    	//把缺失的orderby列补上,union太复杂暂时不支持
    	if(isMasterSelect()){
	    	if(!CollectionUtils.isEmpty(missOrderbyColumns)){
	    		String orderby_columns="";
	    		for(String columnName: missOrderbyColumns){
	    			orderby_columns+= ", "+ columnName;
	    		}
	    		parseResult.getSqlBuilder().buildSQL("select_missing_columns", orderby_columns);
	    	}
	    	//增加一个limit东西限制查询大小
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
        		x.accept(new MySqlOutputVisitor(expression));
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
	        	    //转换SQL：select avg(salary) as avg_salary, (select avg(bonus) from user), count(1) as auto_gen_col_count from user
	        		if(AggregationType.AVG.equals(aggregationType) && !attachCountExpression){
	        			print(", COUNT(1) AS " + AUTO_GEN_COL_COUNT);
	        			attachCountExpression= true;
	        			if(logger.isInfoEnabled()){
	            			logger.info("auto create count(1) AS "+AUTO_GEN_COL_COUNT);
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
	        		throw new SQLParserException("not support select * for sql: "+sql+",please enumerate every column");
	        	}
	        	else{
	        		//可能还是子查询SQLQueryExpr
	        		String columnName= AUTO_GEN_COL + columnIndex;
	        		parseResult.addMetadataColumn(columnName);
	        		StringBuilder expression = new StringBuilder();
	        		expr.accept(new MySqlOutputVisitor(expression));
	        		logger.warn(expression.toString()+" miss alias, will use temporary column name: "+columnName);
	        	}
        	}
        }
        
        /*
    	 * 预留一个位置给那些不在select选项里的oderby列，否则结果集无法按给定的orderby合并排序
    	 * 如 select name from emp order by deptno，需要把deptno 追加到select选项中
    	 * 最终sql：select name ,deptno from emp order by deptno
    	 */
    	parseResult.getSqlBuilder().appendToken("select_missing_columns", false);
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
				//不应该存在groupby列不在select选项里的情况
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
            //默认是升序
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
            	//如果orderby的列不在select选项里，则需要追加处理
            	if(index!= -1){
            		parseResult.addOrderColumn(new OrderColumn(columnName,orderType,++index));
            		if(logger.isInfoEnabled()){
            			logger.info("order column ["+columnName+"] index is: "+index);
            		}
            	}
            	else{
            		//只有当grouby为空的时候才能追加orderby否则sql执行会出粗
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
    
    //解析Limit节点
    @Override
    public boolean visit(MySqlSelectQueryBlock.Limit x) {
    	//如果嵌套子查询或第一个SQL解析完毕
    	if(!isEnableCollectMetadata()){
    		return super.visit(x); 
    	}
		print("LIMIT ");
    	//第一次解析
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
