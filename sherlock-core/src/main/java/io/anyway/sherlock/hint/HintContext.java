package io.anyway.sherlock.hint;

public interface HintContext {
	/**
	 * 获取租户的数据分片名称
	 * @return
	 */
	String getPartitionDBName();
}
