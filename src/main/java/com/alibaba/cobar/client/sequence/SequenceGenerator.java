package com.alibaba.cobar.client.sequence;

public interface SequenceGenerator {
	
	long nextval(String name);
}
