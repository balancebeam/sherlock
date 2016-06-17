package io.pddl.router.table.support;

import java.util.Set;

import org.springframework.util.CollectionUtils;

import io.pddl.router.table.GlobalTableRepository;

/**
 * 全局表仓库
 * @author yangzz
 *
 */
public class GlobalTableRepositorySupport implements GlobalTableRepository{

	private Set<String> globalTables;
	
	@Override
    public boolean isGlobalTable(String name){
    	return !CollectionUtils.isEmpty(globalTables)? globalTables.contains(name) : false;
    }
	
	public void setGlobalTables(Set<String> globalTables){
    	this.globalTables= globalTables;
    }

	@Override
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(globalTables);
	}

}
