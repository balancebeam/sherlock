package com.alibaba.cobar.client.sequence.support;

import com.alibaba.cobar.client.sequence.SequenceGenerator;

public class ZookeeperPartitionSequenceGenerator implements SequenceGenerator{

	@Override
	public long nextval(String name) {
		return 0;
	}

}
