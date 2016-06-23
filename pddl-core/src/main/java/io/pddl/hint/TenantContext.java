package io.pddl.hint;

public interface TenantContext {
	/**
	 * 获取租户的数据分片名称
	 * @return
	 */
	String getPartitionName();
}
