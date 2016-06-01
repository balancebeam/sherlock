package io.pddl.spring.parser;

import java.util.Arrays;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import io.pddl.exception.ShardingTableException;
import io.pddl.router.table.config.LogicTableStrategyConfig;
import io.pddl.router.table.strategy.support.DefaultLogicTableExpressionStrategy;

public class ShardingTableStrategyBeanDefinitionParser extends AbstractBeanDefinitionParser{

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LogicTableStrategyConfig.class);
		factory.addPropertyValue("columns", Arrays.asList(element.getAttribute("columns").split(",")));
		String strategy= element.getAttribute("strategy");
		if(!StringUtils.isEmpty(strategy)){
			factory.addPropertyReference("strategy", strategy);
			return factory.getBeanDefinition();
		}
		String expression= element.getAttribute("expression");
		if(!StringUtils.isEmpty(expression)){
			BeanDefinitionBuilder strategyBuilder = BeanDefinitionBuilder.rootBeanDefinition(DefaultLogicTableExpressionStrategy.class);
			strategyBuilder.addPropertyValue("expression", expression);
			factory.addPropertyValue("strategy", strategyBuilder.getBeanDefinition());
			return factory.getBeanDefinition();
		}
		throw new ShardingTableException("miss expression or strategy");
	}
	
}
