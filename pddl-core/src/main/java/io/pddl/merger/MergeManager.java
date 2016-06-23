package io.pddl.merger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import io.pddl.executor.ExecuteContext;
import io.pddl.sqlparser.bean.AggregationColumn;
import io.pddl.sqlparser.bean.AggregationColumn.AggregationType;
import io.pddl.sqlparser.bean.GroupColumn;
import io.pddl.sqlparser.bean.OrderColumn;

public class MergeManager {

	public ResultSet merge(List<ResultSet> result,ExecuteContext ctx)throws Exception{
		
		if(result.size()==1) return result.get(0);
		//handle groupby
		List<GroupColumn> groups= ctx.getSQLParsedResult().getGroupColumns();
		//是否有聚合操作
		List<AggregationColumn> aggregations= ctx.getSQLParsedResult().getAggregationColumns();
		
		boolean isAggregationable= !CollectionUtils.isEmpty(aggregations);
		
		List<Row> mergeResult= new ArrayList<Row>();
		
		if(!CollectionUtils.isEmpty(groups)){
			Map<String,List<Row>> hash = new HashMap<String,List<Row>>();
			for(ResultSet rs: result){
				while(rs.next()){
					String key="";
					for(GroupColumn c: groups){
						key=","+rs.getObject(c.getColumnIndex());
					}
					//key= key.substring(1);
					if(hash.containsKey(key)){
						//如果没有聚合函数需要做去除记录，此时所有的字段都是groupby字段
						if(isAggregationable){
							hash.get(key).add(createRow(rs));
						}
					}
					else{
						List<Row> rows=new LinkedList<Row>();
						rows.add(createRow(rs));
						hash.put(key, rows);
					}
				}
			}
			if(isAggregationable){
				//除了聚合字段不一样，其他的字段应该是一模一样
				for(List<Row> rows: hash.values()){
					mergeResult.add(mergeAggregation(rows,aggregations,groups));
				}
			}
			else{
				//对于没有归并的做简单的合并
				for(List<Row> rows: hash.values()){
					mergeResult.addAll(rows);
				}
			}
		}
		//只有聚合列，不存在其他的列
		else if(isAggregationable){
			List<Row> rows = new ArrayList<Row>();
			for(ResultSet rs: result){
				rows.add(this.createRow(rs));
			}
			Row oneRow= mergeAggregation(rows,aggregations,Collections.<GroupColumn>emptyList());
			mergeResult.add(oneRow);
		}
		//处理排序问题
		final List<OrderColumn> orderbys= ctx.getSQLParsedResult().getOrderColumns();
		if(!CollectionUtils.isEmpty(orderbys)){
			Collections.sort(mergeResult,new Comparator<Row>(){
				@Override
				public int compare(Row o1, Row o2) {
					for(OrderColumn c: orderbys){
						Object x1= o1.getObject(c.getColumnIndex());
						Object x2= o2.getObject(c.getColumnIndex());
						//TODO 字符串和数字的比较
					}
					
					return 0;
				}
			});
		}
		//处理limit问题
		Limit limit= ctx.getSQLParsedResult().getLimit();
		if(limit!= null){
			int fromIndex= Math.min(mergeResult.size()-1,limit.getOffset());
			int toIndex= Math.max(fromIndex+limit.getRowCount(), mergeResult.size()-1);
			mergeResult= mergeResult.subList(fromIndex, toIndex);
		}
		
		return null;
	}
	
	private Row mergeAggregation(List<Row> rows,List<AggregationColumn> aggregations,List<GroupColumn> groups){
		Row mergeRow= rows.get(0).clone(groups); //克隆一个记录复制grouby字段
		Map<Integer,Object> hash = new HashMap<Integer,Object>();
		for(Row row: rows){
			for(AggregationColumn c: aggregations){
				if(c.getAggregationType()== AggregationType.COUNT){
					long val= (long)row.getObject(c.getColumnIndex());
					if(hash.containsKey(c.getColumnIndex())){
						hash.put(c.getColumnIndex(), val+(long)hash.get(c.getColumnIndex()));
					}
					else{
						hash.put(c.getColumnIndex(),val);
					}
				}
				else if(c.getAggregationType()== AggregationType.SUM){
					double val= (double)row.getObject(c.getColumnIndex());
					if(hash.containsKey(c.getColumnIndex())){
						hash.put(c.getColumnIndex(), val+(double)hash.get(c.getColumnIndex()));
					}
					else{
						hash.put(c.getColumnIndex(),val);
					}
				}
				else if(c.getAggregationType()== AggregationType.MAX){
					double val= (double)row.getObject(c.getColumnIndex());
					if(hash.containsKey(c.getColumnIndex())){
						hash.put(c.getColumnIndex(), Math.max(val,(double)hash.get(c.getColumnIndex())));
					}
					else{
						hash.put(c.getColumnIndex(),val);
					}
				}
				else if(c.getAggregationType()== AggregationType.MIN){
					double val= (double)row.getObject(c.getColumnIndex());
					if(hash.containsKey(c.getColumnIndex())){
						hash.put(c.getColumnIndex(), Math.max(val,(double)hash.get(c.getColumnIndex())));
					}
					else{
						hash.put(c.getColumnIndex(),val);
					}
				}
				else if(c.getAggregationType()== AggregationType.AVG){
					double val= (double)row.getObject(c.getColumnIndex());
					long count= (long)row.getObject("auto_gen_col_count");
					if(hash.containsKey(c.getColumnIndex())){
						Object[] comvals= (Object[])hash.get(c.getColumnIndex());
						//存储sum值
						comvals[0] =count*val+ (double)comvals[0];
						//存储count值
						comvals[1] = count+ (long)comvals[1];
					}
					else{
						hash.put(c.getColumnIndex(),new Object[]{count*val,count});
					}
				}
			}
		}
		//处理最终结果，把他放到row对象中
		for(AggregationColumn c: aggregations){
			Object val= hash.get(c.getColumnIndex());
			if(c.getAggregationType()== AggregationType.AVG){
				Object[] comvals= ((Object[])val);
				val= (double)comvals[0]/(long)comvals[1];
			}
			mergeRow.setValue(c.getColumnIndex(),val);
		}
		return mergeRow;
	}
	
	private Row createRow(ResultSet rs){
		return new Row(rs);
	}
}
