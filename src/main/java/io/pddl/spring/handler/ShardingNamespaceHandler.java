package io.pddl.spring.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import io.pddl.spring.parser.ShardingDataSourceBeanDefinitionParser;
import io.pddl.spring.parser.ShardingTableStrategyBeanDefinitionParser;
import io.pddl.spring.parser.ShardingLogicTableBeanDefinitionParser;

public class ShardingNamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		registerBeanDefinitionParser("db-sharding",new ShardingDataSourceBeanDefinitionParser());
		registerBeanDefinitionParser("table-strategy",new ShardingTableStrategyBeanDefinitionParser());
		registerBeanDefinitionParser("table-sharding",new ShardingLogicTableBeanDefinitionParser());
	}

}
