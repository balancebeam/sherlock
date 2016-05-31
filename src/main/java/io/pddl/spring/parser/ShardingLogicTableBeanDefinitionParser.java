package io.pddl.spring.parser;

import static io.pddl.spring.Constants.TABLE_CHILD;
import static io.pddl.spring.Constants.TABLE_GLOBAL;
import static io.pddl.spring.Constants.TABLE_LOGIC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import io.pddl.table.model.LogicChildTableConfig;
import io.pddl.table.model.LogicPrimaryTableConfig;
import io.pddl.table.support.DefaultShardingTableRepository;

public class ShardingLogicTableBeanDefinitionParser extends AbstractBeanDefinitionParser{

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DefaultShardingTableRepository.class);
		
		List<Element> globalTableElements= DomUtils.getChildElementsByTagName(element, TABLE_GLOBAL);
		if(!CollectionUtils.isEmpty(globalTableElements)){
			Map<String,String[]> globalTableMapping= new HashMap<String,String[]>();
			for(Element it: globalTableElements){
				globalTableMapping.put(it.getAttribute("name"),it.getAttribute("partitions").split(","));
			}
			factory.addPropertyValue("globalTableMapping", globalTableMapping);
		}
		
		List<Element> logicPrimaryTableElements= DomUtils.getChildElementsByTagName(element, TABLE_LOGIC);
		ManagedList<BeanDefinition> logicPrimaryTables= new ManagedList<BeanDefinition>();
		for(Element it: logicPrimaryTableElements){
			logicPrimaryTables.add(parsePrimaryLogicTable(it,parserContext));
		}
		factory.addPropertyValue("logicPrimaryTables", logicPrimaryTables);
		
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parsePrimaryLogicTable(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LogicPrimaryTableConfig.class);
		factory.addPropertyValue("name", element.getAttribute("name"));
		factory.addPropertyValue("primaryKey", element.getAttribute("primaryKey"));
		factory.addPropertyReference("strategy", element.getAttribute("strategy"));
		factory.addPropertyValue("partitions", new HashSet<String>(Arrays.asList(element.getAttribute("partitions").split(","))));
		factory.addPropertyValue("postfixes", Arrays.asList(element.getAttribute("postfixes").split(",")));
		List<Element> childLogicTableElements= DomUtils.getChildElementsByTagName(element, TABLE_CHILD);
		if(!CollectionUtils.isEmpty(childLogicTableElements)){
			ManagedList<BeanDefinition> children= new ManagedList<BeanDefinition>();
			for(Element it: childLogicTableElements){
				children.add(parseChildLogicTable(it,parserContext));
			}
			factory.addPropertyValue("children", children);
		}
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parseChildLogicTable(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LogicChildTableConfig.class);
		factory.addPropertyValue("name", element.getAttribute("name"));
		factory.addPropertyValue("primaryKey", element.getAttribute("primaryKey"));
		factory.addPropertyValue("foreignKey", element.getAttribute("foreignKey"));
		List<Element> childLogicTableElements= DomUtils.getChildElementsByTagName(element, TABLE_CHILD);
		if(!CollectionUtils.isEmpty(childLogicTableElements)){
			ManagedList<BeanDefinition> children= new ManagedList<BeanDefinition>();
			for(Element it: childLogicTableElements){
				children.add(parseChildLogicTable(it,parserContext));
			}
			factory.addPropertyValue("children", children);
		}
		return factory.getBeanDefinition();
	}

}
