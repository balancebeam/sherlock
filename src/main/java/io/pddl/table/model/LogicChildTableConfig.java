package io.pddl.table.model;

import io.pddl.table.LogicChildTable;

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
