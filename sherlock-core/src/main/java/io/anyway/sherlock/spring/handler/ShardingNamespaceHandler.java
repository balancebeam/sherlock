package io.anyway.sherlock.spring.handler;

import io.anyway.sherlock.spring.parser.ShardingTableStrategyBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import io.anyway.sherlock.spring.parser.ShardingDataSourceBeanDefinitionParser;

public class ShardingNamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		registerBeanDefinitionParser("data-source",new ShardingDataSourceBeanDefinitionParser());
		registerBeanDefinitionParser("strategy",new ShardingTableStrategyBeanDefinitionParser());
	}

}
