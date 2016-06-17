package io.pddl.router.table.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import io.pddl.router.table.LogicTable;
import io.pddl.router.table.LogicTableRepository;
import io.pddl.router.table.config.AbstractLogicTableConfig;

/**
 * 逻辑仓库仓库
 * @author yangzz
 *
 */
public class LogicTableRepositorySupport implements LogicTableRepository{

	private Map<String,LogicTable> logicTableMapping= new HashMap<String,LogicTable>();
	
	@Override
	public boolean isLogicChildTable(String tableName) {
		if(!CollectionUtils.isEmpty(logicTableMapping)){
			return logicTableMapping.get(tableName).isChildTable();
		}
		return false;
	}

	@Override
	public LogicTable getLogicTable(String tableName) {
		return logicTableMapping.get(tableName);
	}

	@Override
	public Collection<String> getLogicTableNames() {
		return logicTableMapping.keySet();
	}
	
	public void setLogicTables(List<LogicTable> logicTables){
		for(int i=0;i<logicTables.size();i++){
			forEachLogicTable(logicTables.get(i),String.valueOf(i));
		}
	}
	
	private void forEachLogicTable(LogicTable table,String layerIdx){
		logicTableMapping.put(table.getName(),table);
		((AbstractLogicTableConfig)table).setLayerIdx(layerIdx);
		List<? extends LogicTable> children= table.getChildren();
		if(!CollectionUtils.isEmpty(children)){
			for(int i=0;i<children.size();i++){
				((AbstractLogicTableConfig)children.get(i)).setParent(table);
				forEachLogicTable(children.get(i),layerIdx+","+i);
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(logicTableMapping);
	}

}
