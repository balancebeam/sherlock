package io.pddl.spring.parser;

import static io.pddl.spring.Constants.DB_MASTER;
import static io.pddl.spring.Constants.DB_PARTITION;
import static io.pddl.spring.Constants.DB_PARTITIONS;
import static io.pddl.spring.Constants.DB_READONLY;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import io.pddl.datasource.DatabaseType;
import io.pddl.datasource.support.DefaultDataSourceProxy;
import io.pddl.datasource.support.DefaultPartitionDataSource;
import io.pddl.datasource.support.DefaultShardingDataSource;

public class ShardingDataSourceBeanDefinitionParser extends AbstractBeanDefinitionParser{

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DefaultShardingDataSource.class);
		Element partitionsElement= DomUtils.getChildElementByTagName(element, DB_PARTITIONS);
		List<Element> partitions= DomUtils.getChildElementsByTagName(partitionsElement, DB_PARTITION);
		ManagedSet<BeanDefinition> partitionDataSources  = new ManagedSet<BeanDefinition>();
		for(Element partition: partitions){
			partitionDataSources.add(parsePartitionDataSource(partition,parserContext));
		}
		factory.addPropertyValue("partitionDataSources", partitionDataSources);
		factory.addPropertyValue("databaseType", DatabaseType.valueOf(element.getAttribute("dbType")));
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parsePartitionDataSource(Element element, ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DefaultPartitionDataSource.class);
		factory.addPropertyValue("name", element.getAttribute("name"));
		String poolSize= element.getAttribute("poolSize");
		if(!StringUtils.isEmpty(poolSize)){
			factory.addPropertyValue("poolSize", Integer.parseInt(poolSize));
		}
		String readStrategy= element.getAttribute("readStrategy");
		if(!StringUtils.isEmpty(readStrategy)){
			factory.addPropertyValue("readStrategy", readStrategy);
		}
		
		Element write= DomUtils.getChildElementByTagName(element, DB_MASTER);
		factory.addPropertyValue("writeDataSource", parseDataSourceDescriptor(write,parserContext));
		
		List<Element> reads= DomUtils.getChildElementsByTagName(element, DB_READONLY);
		if(!CollectionUtils.isEmpty(reads)){
			ManagedList<BeanDefinition> readDataSources= new ManagedList<BeanDefinition>(reads.size());
			for(Element read: reads){
				readDataSources.add(parseDataSourceDescriptor(read,parserContext));
			}
			factory.addPropertyValue("readDataSources",readDataSources);
		}
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parseDataSourceDescriptor(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DefaultDataSourceProxy.class);
		factory.addConstructorArgReference(element.getAttribute("dataSource"));
		String weight= element.getAttribute("weight");
		if(!StringUtils.isEmpty(weight)){
			factory.addPropertyValue("weight", Integer.parseInt(weight));
		}
		return factory.getBeanDefinition();
	}
	
}
