package io.pddl.table.exception;

import com.alibaba.cobar.client.exception.ShardingException;

@SuppressWarnings("serial")
public class ShardingTableException extends ShardingException{

	public ShardingTableException(String message) {
		super(message);
	}

}
