package io.pddl.spring.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import io.pddl.spring.parser.ShardingDataSourceBeanDefinitionParser;
import io.pddl.spring.parser.ShardingTableStrategyBeanDefinitionParser;

public class ShardingNamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		registerBeanDefinitionParser("data-source",new ShardingDataSourceBeanDefinitionParser());
		registerBeanDefinitionParser("strategy",new ShardingTableStrategyBeanDefinitionParser());
	}

}
