package io.pddl.router.table.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import io.pddl.router.table.GlobalTableRepository;

/**
 * 全局表仓库
 * @author yangzz
 *
 */
public class GlobalTableRepositorySupport implements GlobalTableRepository{

	private Map<String,List<String>> globalTables;
	
	@Override
    public boolean isGlobalTable(String name){
    	return !CollectionUtils.isEmpty(globalTables)? globalTables.containsKey(name) : false;
    }

	@Override
	public List<String> getPartitionDataSourceNames(String name) {
		return globalTables.get(name);
	}

	public void setGlobalTables(Map<String,List<String>> globalTables){
    	this.globalTables= globalTables;
    }

	@Override
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(globalTables);
	}

}
