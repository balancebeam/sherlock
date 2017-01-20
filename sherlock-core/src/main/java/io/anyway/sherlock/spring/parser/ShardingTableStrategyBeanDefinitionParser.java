package io.anyway.sherlock.spring.parser;

import java.util.Arrays;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import io.anyway.sherlock.exception.ShardingTableException;
import io.anyway.sherlock.router.strategy.config.ShardingStrategyConfig;
import io.anyway.sherlock.router.strategy.support.ExpressionShardingStrategySupport;

public class ShardingTableStrategyBeanDefinitionParser extends AbstractBeanDefinitionParser{

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ShardingStrategyConfig.class);
		factory.addPropertyValue("columns", Arrays.asList(element.getAttribute("sharding-columns").split(",")));
		String strategy= element.getAttribute("strategy");
		if(!StringUtils.isEmpty(strategy)){
			factory.addPropertyReference("strategy", strategy);
			return factory.getBeanDefinition();
		}
		String expression= element.getAttribute("expression");
		if(!StringUtils.isEmpty(expression)){
			BeanDefinitionBuilder strategyBuilder = BeanDefinitionBuilder.rootBeanDefinition(ExpressionShardingStrategySupport.class);
			strategyBuilder.addPropertyValue("expression", expression);
			factory.addPropertyValue("strategy", strategyBuilder.getBeanDefinition());
			return factory.getBeanDefinition();
		}
		throw new ShardingTableException("miss expression or strategy");
	}
	
}
