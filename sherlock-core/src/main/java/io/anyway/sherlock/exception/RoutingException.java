package io.anyway.sherlock.exception;

public class RoutingException extends ShardingException {
	private static final long serialVersionUID = 8980219652872668164L;

	public RoutingException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public RoutingException(String msg)
	{
		super(msg);
	}

}
