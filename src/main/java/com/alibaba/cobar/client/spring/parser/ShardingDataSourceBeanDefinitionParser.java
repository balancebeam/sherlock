package com.alibaba.cobar.client.spring.parser;

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

import com.alibaba.cobar.client.datasources.DataSourceDescriptor;
import com.alibaba.cobar.client.datasources.DefaultShardingDataSource;
import com.alibaba.cobar.client.datasources.PartitionDataSource;

public class ShardingDataSourceBeanDefinitionParser extends AbstractBeanDefinitionParser{

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DefaultShardingDataSource.class);
		Element partitionsElement= DomUtils.getChildElementByTagName(element, "db-partitions");
		List<Element> partitions= DomUtils.getChildElementsByTagName(partitionsElement, "db-partition");
		ManagedSet<BeanDefinition> partitionDataSources  = new ManagedSet<BeanDefinition>();
		for(Element partition: partitions){
			partitionDataSources.add(parsePartitionDataSource(partition,parserContext));
		}
		factory.addPropertyValue("partitionDataSources", partitionDataSources);
		
		Element readStrategyElement= DomUtils.getChildElementByTagName(element, "read-strategy");
		if(readStrategyElement!= null){
			String repository= readStrategyElement.getAttribute("repository");
			factory.addPropertyReference("readStrategyRepository", repository);
		}
		
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parsePartitionDataSource(Element element, ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(PartitionDataSource.class);
		factory.addPropertyValue("name", element.getAttribute("name"));
		String poolSize= element.getAttribute("poolSize");
		if(!StringUtils.isEmpty(poolSize)){
			factory.addPropertyValue("poolSize", Integer.parseInt(poolSize));
		}
		String readStrategy= element.getAttribute("readStrategy");
		if(!StringUtils.isEmpty(readStrategy)){
			factory.addPropertyValue("readStrategy", readStrategy);
		}
		
		Element write= DomUtils.getChildElementByTagName(element, "ds-write");
		factory.addPropertyValue("writeDataSource", parseDataSourceDescriptor(write,parserContext));
		
		List<Element> reads= DomUtils.getChildElementsByTagName(element, "ds-read");
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
		String ref= element.getAttribute("ref");
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DataSourceDescriptor.class);
		factory.addPropertyReference("dataSource", ref);
		String weight= element.getAttribute("weight");
		if(!StringUtils.isEmpty(weight)){
			factory.addPropertyValue("weight", Integer.parseInt(weight));
		}
		return factory.getBeanDefinition();
	}
	
}
