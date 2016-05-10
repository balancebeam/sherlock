package com.alibaba.cobar.client.sequence.support;

import com.alibaba.cobar.client.sequence.SequenceGenerator;

public class PostgreSQLNextvalSequenceGenerator implements SequenceGenerator{

	@Override
	public long nextval(String name) {
		return 0;
	}

}
