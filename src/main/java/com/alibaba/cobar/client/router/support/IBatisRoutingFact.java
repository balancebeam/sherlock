/**
 * Copyright 1999-2011 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.alibaba.cobar.client.router.support;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * A Wrapper for ibatis-based routing fact.
 * 
 * @author fujohnwang
 * @since  1.0
 */
public class IBatisRoutingFact {
	// SQL identity
	private String action;
	// the argument of SQL action
	private Object argument;
	
	private SqlMapClient sqlMapClient;
	
	public IBatisRoutingFact(){}
	public IBatisRoutingFact(String action, Object arg,SqlMapClient sqlMapClient){
		this.action   = action;
		this.argument = arg;
		this.sqlMapClient= sqlMapClient;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Object getArgument() {
		return argument;
	}
	public void setArgument(Object argument) {
		this.argument = argument;
	}
	
	public void setSqlMapClient(SqlMapClient sqlMapClient){
		this.sqlMapClient= sqlMapClient;
	}
	public SqlMapClient getSqlMapClient(){
		return sqlMapClient;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((argument == null) ? 0 : argument.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IBatisRoutingFact other = (IBatisRoutingFact) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "IBatisRoutingFact [action=" + action + ", argument=" + argument
				+ "]";
	}
	
}
