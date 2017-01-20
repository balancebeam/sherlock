package io.anyway.sherlock.router.table.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import io.anyway.sherlock.router.table.LogicTable;
import io.anyway.sherlock.router.table.LogicTableRepository;
import io.anyway.sherlock.router.table.config.AbstractLogicTableConfig;

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
		for(LogicTable each: logicTables){
			forEachLogicTable(each);
		}
	}
	
	private void forEachLogicTable(final LogicTable table){
		logicTableMapping.put(table.getName(),table);
		List<? extends LogicTable> children= table.getChildren();
		if(!CollectionUtils.isEmpty(children)){
			for(LogicTable each: children){
				((AbstractLogicTableConfig)each).setParent(table);
				forEachLogicTable(each);
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(logicTableMapping);
	}

}
