package io.anyway.sherlock.router.table.config;

import io.anyway.sherlock.router.table.LogicChildTable;

public class LogicChildTableConfig extends AbstractLogicTableConfig implements LogicChildTable{

	private String foreignKey;
	
	public void setForeignKey(String foreignKey){
		this.foreignKey= foreignKey;
	}
	
	@Override
	public String getForeignKey(){
		return foreignKey;
	}
}
