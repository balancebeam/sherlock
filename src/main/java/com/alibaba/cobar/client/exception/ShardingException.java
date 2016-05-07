package com.alibaba.cobar.client.exception;

@SuppressWarnings("serial")
public class ShardingException extends RuntimeException{

	public ShardingException(String message){
		super(message);
	}
	
	public ShardingException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public ShardingException(Throwable cause) {
        super(cause);
    }
	
	public void handle(){}
	
}
