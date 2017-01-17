package io.pddl.merger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import io.pddl.executor.ExecuteContext;
import io.pddl.sqlparser.SQLParsedResult;
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
			//先获取分组键序列
			List<Integer> keys= new ArrayList<Integer>(groups.size());
			for(GroupColumn c: groups){
				keys.add(c.getColumnIndex());
			}
			//如果分组中有聚合操作
			if(isAggregationable){
				Map<String,GroupAggreRow> rowsMapping = new HashMap<String,GroupAggreRow>();
				for(ResultSet rs: result){
					while(rs.next()){
						String key= getKey(rs,keys);
						if(!rowsMapping.containsKey(key)){
							rowsMapping.put(key, new GroupAggreRow(rs,ctx.getSQLParsedResult()));
						}
						Map<Integer,Object> aggreHash= rowsMapping.get(key).getAggreHash();
						//处理聚合操作
						for(AggregationColumn each: aggregations){
							aggreMergerRepository.get(each.getAggregationType()).merge(rs, aggreHash, each.getColumnIndex());
						}
					}
				}
				//等所有结果处理完之后
				for(GroupAggreRow each: rowsMapping.values()){
					//处理最终结果，把他放到row对象中
					mergeAggre2Row(aggregations,each.getRow(),each.getAggreHash());
					mergeResult.add(each.getRow());
				}
			}
			else{
				Set<String> unique= new HashSet<String>();
				for(ResultSet rs: result){
					while(rs.next()){
						String key= getKey(rs,keys);
						if(!unique.contains(key)){
							unique.add(key);
							mergeResult.add(new Row(rs,ctx.getSQLParsedResult()));
						}
						else{
							//去掉多余的记录，什么也不做
						}
					}
				}
			}
		}
		//只有聚合列，不存在其他的列
		else if(isAggregationable){
			Row row = new Row();
			Map<Integer,Object> aggreHash= new HashMap<Integer,Object>();
			for(ResultSet rs: result){
				while(rs.next()){
					//处理聚合操作
					for(AggregationColumn each: aggregations){
						aggreMergerRepository.get(each.getAggregationType()).merge(rs, aggreHash, each.getColumnIndex());
					}
				}
			}
			mergeAggre2Row(aggregations,row,aggreHash);
			mergeResult.add(row);
		}
		else if(ctx.getSQLParsedResult().distinct()){
			Set<String> unique= new HashSet<String>();
			List<Integer> keys= new ArrayList<Integer>(ctx.getSQLParsedResult().getMetadataColumns().size());
			for(int i=1;i<=ctx.getSQLParsedResult().getMetadataColumns().size();i++){
				keys.add(i);
			}
			for(ResultSet rs: result){
				while(rs.next()){
					String key= getKey(rs,keys);
					if(!unique.contains(key)){
						unique.add(key);
						mergeResult.add(new Row(rs,ctx.getSQLParsedResult()));
					}
					else{
						//去掉多余的记录，什么也不做
					}
				}
			}
		}
		//处理排序问题
		final List<OrderColumn> orderbys= ctx.getSQLParsedResult().getOrderColumns();
		if(!CollectionUtils.isEmpty(orderbys)){
			Collections.sort(mergeResult,new Comparator<Row>(){
				@Override
				public int compare(Row o1, Row o2) {
//					for(OrderColumn c: orderbys){
//						Object x1= o1.getObject(c.getColumnIndex());
//						Object x2= o2.getObject(c.getColumnIndex());
//						
//					}
					
					return 0;
				}
			});
		}
		//处理limit问题
		Limit limit= ctx.getSQLParsedResult().getLimit();
		if(limit!= null){
			int fromIndex= Math.min(mergeResult.size()-1,limit.getOffset());
			int toIndex= Math.min(fromIndex+limit.getRowCount(), mergeResult.size()-1);
			mergeResult= mergeResult.subList(fromIndex, toIndex);
		}
		
		return null;
	}
	
	//把聚合结果放到Row中
	private void mergeAggre2Row(List<AggregationColumn> aggregations,Row mergeRow,Map<Integer,Object> aggreHash){
		for(AggregationColumn c: aggregations){
			Object val= aggreHash.get(c.getColumnIndex());
			if(c.getAggregationType()== AggregationType.AVG){
				Object[] comvals= ((Object[])val);
				val= (Double)comvals[0]/(Long)comvals[1];
			}
			mergeRow.setValue(c.getColumnIndex(),val);
		}
	}
	//根据columnindex组合主键
	private String getKey(ResultSet rs,List<Integer> columnIndexs)throws SQLException{
		String key="";
		for(int index: columnIndexs){
			key+=","+rs.getObject(index);
		}
		return key.substring(1);
	}
	
	private class GroupAggreRow{
		private Row row;
		private Map<Integer,Object> aggreHash;
		public GroupAggreRow(ResultSet rs,SQLParsedResult parserResult){
			row= new Row(rs,parserResult);
			aggreHash= new HashMap<Integer,Object>();
		}
		
		public Row getRow(){
			return row;
		}
		
		public Map<Integer,Object> getAggreHash(){
			return aggreHash;
		}
	};
	
	
	private interface AggreMerger{
		void merge(ResultSet rs,Map<Integer,Object> aggreHash,int columnIndex)throws SQLException;
	}
	
	private Map<AggregationType,AggreMerger> aggreMergerRepository= new HashMap<AggregationType,AggreMerger>();
	
	{
		//总数处理
		aggreMergerRepository.put(AggregationType.COUNT, new AggreMerger(){
			@Override
			public void merge(ResultSet rs,Map<Integer,Object> aggreHash, int columnIndex) throws SQLException{
				long val= (Long)rs.getObject(columnIndex);
				if(aggreHash.containsKey(columnIndex)){
					aggreHash.put(columnIndex, val+(Long)aggreHash.get(columnIndex));
				}
				else{
					aggreHash.put(columnIndex,val);
				}
			}
		});
		//总和处理
		aggreMergerRepository.put(AggregationType.SUM, new AggreMerger(){
			@Override
			public void merge(ResultSet rs, Map<Integer, Object> aggreHash, int columnIndex) throws SQLException {
				double val= (Double)rs.getObject(columnIndex);
				if(aggreHash.containsKey(columnIndex)){
					aggreHash.put(columnIndex, val+(Double)aggreHash.get(columnIndex));
				}
				else{
					aggreHash.put(columnIndex,val);
				}
			}
		});
		//最大值
		aggreMergerRepository.put(AggregationType.MAX, new AggreMerger(){
			@Override
			public void merge(ResultSet rs, Map<Integer, Object> aggreHash, int columnIndex) throws SQLException {
				double val= (Double)rs.getObject(columnIndex);
				if(aggreHash.containsKey(columnIndex)){
					aggreHash.put(columnIndex, Math.max(val,(Double)aggreHash.get(columnIndex)));
				}
				else{
					aggreHash.put(columnIndex,val);
				}
			}
		});
		
		//最小值
		aggreMergerRepository.put(AggregationType.MIN, new AggreMerger(){
			@Override
			public void merge(ResultSet rs, Map<Integer, Object> aggreHash, int columnIndex) throws SQLException {
				double val= (Double)rs.getObject(columnIndex);
				if(aggreHash.containsKey(columnIndex)){
					aggreHash.put(columnIndex, Math.min(val,(Double)aggreHash.get(columnIndex)));
				}
				else{
					aggreHash.put(columnIndex,val);
				}
			}
		});
		//平局值
		aggreMergerRepository.put(AggregationType.AVG, new AggreMerger(){
			@Override
			public void merge(ResultSet rs, Map<Integer, Object> aggreHash, int columnIndex) throws SQLException {
				double val= (Double)rs.getObject(columnIndex);
				long count= (Long)rs.getObject("auto_gen_col_count");
				if(aggreHash.containsKey(columnIndex)){
					Object[] comvals= (Object[])aggreHash.get(columnIndex);
					//存储sum值
					comvals[0] =count*val+ (Double)comvals[0];
					//存储count值
					comvals[1] = count+ (Long)comvals[1];
				}
				else{
					aggreHash.put(columnIndex,new Object[]{count*val,count});
				}
			}
		});
	}
}
