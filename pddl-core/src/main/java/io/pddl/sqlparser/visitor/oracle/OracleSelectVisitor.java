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
 * Oralce鏌ヨ璁块棶鏀堕泦鍣紝涓昏鏀堕泦琛ㄥ悕銆佹潯浠堕」銆佸垪鍏冩暟鎹�乨istinct銆佽仛鍚堣〃杈惧紡銆乬roupby銆乷rderby鍜宭imit绛夊唴瀹�
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
	
    //閬嶅巻琛ㄥ悕
    @Override
    public boolean visit(final OracleSelectQueryBlock x) {
    	selectLayer++;
        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableExpr = (SQLExprTableSource) x.getFrom();
            setCurrentTable(tableExpr.getExpr().toString(), Optional.fromNullable(tableExpr.getAlias()));
        }
        //澶勭悊distinct
        if(isEnableCollectMetadata()){
        	if(SQLSetQuantifier.DISTINCT==x.getDistionOption()){
        		parseResult.markDistinct();
        	}
        }
        return super.visit(x);
    }
    
    @Override
    public void endVisit(final OracleSelectQueryBlock x) {
    	//鎶婄己澶辩殑orderby鍒楄ˉ涓�,union澶鏉傛殏鏃朵笉鏀寔
    	if(isMasterSelect()){
	    	if(!CollectionUtils.isEmpty(missOrderbyColumns)){
	    		String orderby_columns="";
	    		for(String columnName: missOrderbyColumns){
	    			orderby_columns+= ", "+ columnName;
	    		}
	    		parseResult.getSqlBuilder().buildSQL("select_missing_columns", orderby_columns);
	    	}
	    	//澧炲姞涓�涓猯imit涓滆タ闄愬埗鏌ヨ澶у皬
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
     * 涓昏灞忚斀宓屽瀛愭煡璇㈠拰UNIO鍚庤竟鏌ヨ鐨勪笢瑗�
     * 
     * 鍦烘櫙涓�锛歴elect a ,(select b from ...) from ... where xx= (select xx from ...); 涓嶅鐞嗗祵濂楀瓙鏌ヨ
     * 鍦烘櫙浜岋細(select xxx from yyy) UNION [ALL] (select xxx from yyy2); 涓嶅鐞哢NION 鍚庤竟鐨勬煡璇�
     */
    private boolean isEnableCollectMetadata(){
    	return !finishCollectMetadata && selectLayer == 1;
    }
    
    private boolean isMasterSelect(){
    	return selectLayer == 1;
    }
    
    /*
     * 鍙亶鍘嗙涓�绾ELECT閫夋嫨椤癸紝鏀堕泦Metadata淇℃伅锛屽悓鏃舵敹闆哅AX銆丮IN銆丆OUNT銆丼UM鍜孉VG鎿嶄綔锛涘缓璁瓙鏌ヨ鍜岃〃杈惧紡浣跨敤alias鍒悕
     */
    @Override
    protected void printSelectList(List<SQLSelectItem> selectList) {
        super.printSelectList(selectList);
        //濡傛灉鏄祵濂楀瓙鏌ヨ鐩存帴婊よ繃
        if(!isMasterSelect()){
        	return;
        }
        int columnIndex= 0;
        //閬嶅巻绗竴绾ELECT閫夋嫨椤�
        for(SQLSelectItem each: selectList){
        	columnIndex++;
        	String alias= each.getAlias();
        	SQLExpr expr= each.getExpr();
        	//濡傛灉鏄〃杈惧紡
        	if(expr instanceof SQLAggregateExpr){
        		SQLAggregateExpr x= (SQLAggregateExpr)expr;
        		StringBuilder expression = new StringBuilder();
        		x.accept(new OracleOutputVisitor(expression));
        		try{
        			//濡傛灉涓嶆槸AggregationType鏋氫妇涓殑鍐呭锛屾姏寮傚父鐩存帴璺宠繃涓嶅仛澶勭悊
	        		AggregationType aggregationType = AggregationType.valueOf(x.getMethodName().toUpperCase());
	        		//瑙ｆ瀽杩囦竴閬嶅氨涓嶅湪瑙ｆ瀽锛屾娈甸�昏緫涓昏澶勭悊濡備笅鍦烘櫙
	        		//(select avg(salary) from emp where userid<500) union (select avg(salary) from emp where userid>100)
	        		//瑙ｆ瀽瀹岀涓�涓猻elect鐨勫厓鏁版嵁灏变笉鍦ㄨВ鏋愶紝浣嗘槸瀵逛簬union鍚庨潰鐨剆elect璇彞锛宎vg鑱氬悎瀛楁涔熻杩藉姞 count(1)鍒�
	        		if(isEnableCollectMetadata()){
		        		AggregationColumn aggregationColumn= new AggregationColumn(expression.toString(),columnIndex,aggregationType);
		        		parseResult.addAggregationColumn(aggregationColumn);
		        		if(logger.isInfoEnabled()){
		        			logger.info("AggregationColumn: "+aggregationColumn);
		        		}
	        		}
	        		//鍦ㄦ湁琛ㄨ揪寮忕殑SQLSelectItem鍚庨潰鍔犱笂鍙拷鍔犱竴涓狢OUNT(1) 鍗冲彲锛岀劧鍚庨�氳繃(avg1*count1+avg2*count2)/(count1+count2)姹傛渶缁堝钩鍧囧��
	        		//鍘熷SQL锛歴elect avg(salary) as avg_salary, (select avg(bonus) from user) from user  
	        	    //杞崲SQL锛歴elect avg(salary) as avg_salary, (select avg(bonus) from user), count(1) as auto_gen_col_count from user
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
        	
        	//鍙互鏀堕泦鍒楀厓鏁版嵁淇℃伅
        	if(isEnableCollectMetadata()){
	        	//濡傛灉鍒悕涓嶄负绌猴紝鍒欏瓨鍌ㄥ埆鍚�
	        	if(!StringUtils.isEmpty(alias)){
	        		parseResult.addMetadataColumn(alias);
	        		continue;
	        	}
	        	//濡傛灉鏄叾浠栧垪鍚�
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
	        		//鍙兘杩樻槸瀛愭煡璇QLQueryExpr
	        		String columnName= AUTO_GEN_COL + columnIndex;
	        		parseResult.addMetadataColumn(columnName);
	        		StringBuilder expression = new StringBuilder();
	        		expr.accept(new OracleOutputVisitor(expression));
	        		logger.warn(expression.toString()+" miss alias, will use temporary column name: "+columnName);
	        	}
        	}
        }
        
        /*
    	 * 棰勭暀涓�涓綅缃粰閭ｄ簺涓嶅湪select閫夐」閲岀殑oderby鍒楋紝鍚﹀垯缁撴灉闆嗘棤娉曟寜缁欏畾鐨刼rderby鍚堝苟鎺掑簭
    	 * 濡� select name from emp order by deptno锛岄渶瑕佹妸deptno 杩藉姞鍒皊elect閫夐」涓�
    	 * 鏈�缁坰ql锛歴elect name ,deptno from emp order by deptno
    	 */
    	parseResult.getSqlBuilder().appendToken("select_missing_columns", false);
    }
    
    //瑙ｆ瀽Groupby鑺傜偣
    @Override
    public boolean visit(SQLSelectGroupByClause x){
    	//濡傛灉宓屽瀛愭煡璇㈡垨绗竴涓猄QL瑙ｆ瀽瀹屾瘯
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
				//涓嶅簲璇ュ瓨鍦╣roupby鍒椾笉鍦╯elect閫夐」閲岀殑鎯呭喌
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
     * 閬嶅巻orderby鍐呭
     */
    @Override
    public boolean visit(SQLOrderBy x) {
    	//濡傛灉宓屽瀛愭煡璇㈡垨绗竴涓猄QL瑙ｆ瀽瀹屾瘯
    	if(!isEnableCollectMetadata()){
    		return super.visit(x);
    	}
    	for (SQLSelectOrderByItem each : x.getItems()) {
            SQLExpr expr = each.getExpr();
            //榛樿鏄崌搴�
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
            	//濡傛灉orderby鐨勫垪涓嶅湪select閫夐」閲岋紝鍒欓渶瑕佽拷鍔犲鐞�
            	if(index!= -1){
            		parseResult.addOrderColumn(new OrderColumn(columnName,orderType,++index));
            		if(logger.isInfoEnabled()){
            			logger.info("order column ["+columnName+"] index is: "+index);
            		}
            	}
            	else{
            		//鍙湁褰揼rouby涓虹┖鐨勬椂鍊欐墠鑳借拷鍔爋rderby鍚﹀垯sql鎵ц浼氬嚭绮�
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
