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
import com.alibaba.cobar.client.exception.ShardingException;

public class ShardingDataSourceBeanDefinitionParser extends AbstractBeanDefinitionParser{

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DefaultShardingDataSource.class);
		List<Element> partitions= DomUtils.getChildElementsByTagName(element, "db-partition");
		ManagedSet<BeanDefinition> partitionDataSources  = new ManagedSet<BeanDefinition>();
		for(Element partition: partitions){
			partitionDataSources.add(parsePartitionDataSource(partition,parserContext));
		}
		factory.addPropertyValue("partitionDataSources", partitionDataSources);
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parsePartitionDataSource(Element element, ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(PartitionDataSource.class);
		String name= element.getAttribute("name");
		if(StringUtils.isEmpty(name)){
			throw new ShardingException("partition name should not empty.");
		}
		factory.addPropertyValue("name", element.getAttribute("name"));
		String readWriteStrategy= element.getAttribute("readWriteStrategy");
		if(!StringUtils.isEmpty(readWriteStrategy)){
			factory.addPropertyValue("readWriteStrategy", Integer.parseInt(readWriteStrategy));
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
