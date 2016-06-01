package io.pddl.router.table.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pddl.router.table.GlobalTableRepository;
import io.pddl.router.table.LogicPrimaryTable;
import io.pddl.router.table.LogicTable;
import io.pddl.router.table.LogicTableRepository;
import io.pddl.router.table.config.AbstractLogicTableConfig;
import io.pddl.util.CollectionUtils;
import io.pddl.util.MapUtils;

public class DefaultShardingTableRepository implements GlobalTableRepository,LogicTableRepository{

	private Map<String,List<String>> globalTableMapping;
	
	private Map<String,LogicTable> logicTableMapping= new HashMap<String,LogicTable>();
	
	public void setGlobalTableMapping(Map<String,List<String>> globalTableMapping){
		this.globalTableMapping= globalTableMapping;
	}

	@Override
	public boolean isGlobalTableEmpty() {
		return MapUtils.isEmpty(globalTableMapping);
	}

	@Override
	public Collection<String> getDBPartitionNames(String tableName) {
		if(isGlobalTableEmpty()){
			return null;
		}
		return globalTableMapping.get(tableName);
	}
	
	@Override
	public Collection<String> getGobalTableNames(){
		if(isGlobalTableEmpty()){
			return null;
		}
		return globalTableMapping.keySet();
	}
	
	public void setLogicPrimaryTables(List<LogicPrimaryTable> logicPrimaryTables){
		for(int i=0;i<logicPrimaryTables.size();i++){
			forEachLogicTable(logicPrimaryTables.get(i),String.valueOf(i));
		}
	}
	
	private void forEachLogicTable(LogicTable table,String hierarchical){
		logicTableMapping.put(table.getName(),table);
		((AbstractLogicTableConfig)table).setHierarchical(hierarchical);
		List<? extends LogicTable> children= table.getChildren();
		if(CollectionUtils.isNotEmpty(children)){
			for(int i=0;i<children.size();i++){
				((AbstractLogicTableConfig)children.get(i)).setParent(table);
				forEachLogicTable(children.get(i),hierarchical+","+i);
			}
		}
	}
	
	@Override
	public boolean isLogicTableEmpty(){
		return MapUtils.isEmpty(logicTableMapping);
	}
	
	@Override
	public boolean isLogicPrimaryTable(String tableName){
		if(MapUtils.isNotEmpty(logicTableMapping)){
			return logicTableMapping.get(tableName).isPrimaryTable();
		}
		return false;
	}
	
	@Override
	public LogicTable getLogicTable(String tableName){
		return logicTableMapping.get(tableName);
	}
	
	@Override
	public Collection<String> getLogicTableNames(){
		return logicTableMapping.keySet();
	}
}
