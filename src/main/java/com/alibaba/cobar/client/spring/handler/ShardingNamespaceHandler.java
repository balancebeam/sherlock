package com.alibaba.cobar.client.spring.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.alibaba.cobar.client.spring.parser.ShardingDataSourceBeanDefinitionParser;

public class ShardingNamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		registerBeanDefinitionParser("db-sharding",new ShardingDataSourceBeanDefinitionParser());
	}

}
