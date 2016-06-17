package io.pddl.spring.parser;

import static io.pddl.spring.Constants.DATABASE_ROUTER;
import static io.pddl.spring.Constants.DATA_BASE_STRATEGY;
import static io.pddl.spring.Constants.DATA_BASE_TYPE;
import static io.pddl.spring.Constants.DATA_SOURCE_NAME;
import static io.pddl.spring.Constants.DATA_SOURCE_PARTITION;
import static io.pddl.spring.Constants.DATA_SOURCE_PARTITIONS;
import static io.pddl.spring.Constants.DATA_SOURCE_REF;
import static io.pddl.spring.Constants.DATA_SOURCE_WEIGHT;
import static io.pddl.spring.Constants.FOREIGN_KEY;
import static io.pddl.spring.Constants.GLOBAL_TABLE;
import static io.pddl.spring.Constants.LOGIC_CHILD_TABLE;
import static io.pddl.spring.Constants.LOGIC_TABLE;
import static io.pddl.spring.Constants.MASTER_DATA_SOURCE;
import static io.pddl.spring.Constants.POOL_SIZE;
import static io.pddl.spring.Constants.PRIMARY_KEY;
import static io.pddl.spring.Constants.READ_STRATEGY;
import static io.pddl.spring.Constants.SHARDING_CACHE;
import static io.pddl.spring.Constants.SLAVE_DATA_SOURCE;
import static io.pddl.spring.Constants.TABLES;
import static io.pddl.spring.Constants.TABLE_NAME;
import static io.pddl.spring.Constants.TABLE_POSTFIXES;
import static io.pddl.spring.Constants.TABLE_STRATEGY;
import static io.pddl.spring.Constants.TIME_OUT;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import io.pddl.datasource.support.PartitionDataSourceSupport;
import io.pddl.datasource.support.ShardingDataSourceRepositorySupport;
import io.pddl.datasource.support.WeightDataSourceProxy;
import io.pddl.executor.support.ExecuteProcessorSupport;
import io.pddl.jdbc.ShardingDataSource;
import io.pddl.router.database.support.DatabaseRouterSupport;
import io.pddl.router.support.SQLRouterSupport;
import io.pddl.router.table.config.LogicChildTableConfig;
import io.pddl.router.table.config.LogicTableConfig;
import io.pddl.router.table.support.GlobalTableRepositorySupport;
import io.pddl.router.table.support.LogicTableRepositorySupport;
import io.pddl.router.table.support.LogicTableRouterSupport;

/**
 * 定义分片数据源
 * @author yangzz
 *
 */
public class ShardingDataSourceBeanDefinitionParser extends AbstractBeanDefinitionParser{
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private BeanDefinition shardingDataSourceRepositoryDefinition;
	
	private BeanDefinition globalTableRepositoryDefinition;
	
	private BeanDefinition logicTableRepositoryDefinition;

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ShardingDataSource.class);
		
		parseShardingDataSource(element,parserContext);
		parseShardingTables(element,parserContext);
		
		factory.addPropertyValue("shardingDataSourceRepository", shardingDataSourceRepositoryDefinition);
		factory.addPropertyValue("globalTableRepository", globalTableRepositoryDefinition);
		factory.addPropertyValue("logicTableRepository", logicTableRepositoryDefinition);
		factory.addPropertyValue("sqlRouter", parseSQLRouter(element,parserContext));
		factory.addPropertyValue("processor", parseExecutorProcessor());
		
		return factory.getBeanDefinition();
	}
	
	private void parseShardingDataSource(Element element, ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ShardingDataSourceRepositorySupport.class);
		Element partitionsElement= DomUtils.getChildElementByTagName(element, DATA_SOURCE_PARTITIONS);
		List<Element> partitions= DomUtils.getChildElementsByTagName(partitionsElement, DATA_SOURCE_PARTITION);
		ManagedSet<BeanDefinition> partitionDataSources  = new ManagedSet<BeanDefinition>();
		for(Element partition: partitions){
			partitionDataSources.add(parseDataSourcePartition(partition,parserContext));
		}
		factory.addPropertyValue("partitionDataSources", partitionDataSources);
		factory.addPropertyValue("databaseType", DatabaseType.valueOf(element.getAttribute(DATA_BASE_TYPE)));
		shardingDataSourceRepositoryDefinition= factory.getBeanDefinition();
	}
	
	private BeanDefinition parseDataSourcePartition(Element element, ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(PartitionDataSourceSupport.class);
		factory.addPropertyValue("name", element.getAttribute(DATA_SOURCE_NAME));
		String poolSize= element.getAttribute(POOL_SIZE);
		if(!StringUtils.isEmpty(poolSize)){
			factory.addPropertyValue("poolSize", Integer.parseInt(poolSize));
		}
		String timeout= element.getAttribute(TIME_OUT);
		if(!StringUtils.isEmpty(timeout)){
			factory.addPropertyValue("timeout", Integer.parseInt(timeout));
		}
		String readStrategy= element.getAttribute(READ_STRATEGY);
		if(!StringUtils.isEmpty(readStrategy)){
			factory.addPropertyValue("readStrategy", readStrategy);
		}
		
		Element master= DomUtils.getChildElementByTagName(element, MASTER_DATA_SOURCE);
		factory.addPropertyValue("masterDataSource", parseDataSourceDescriptor(master,parserContext));
		
		List<Element> slaves= DomUtils.getChildElementsByTagName(element, SLAVE_DATA_SOURCE);
		if(!CollectionUtils.isEmpty(slaves)){
			ManagedList<BeanDefinition> slaveDataSources= new ManagedList<BeanDefinition>(slaves.size());
			for(Element slave: slaves){
				slaveDataSources.add(parseDataSourceDescriptor(slave,parserContext));
			}
			factory.addPropertyValue("slaveDataSources",slaveDataSources);
		}
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parseDataSourceDescriptor(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(WeightDataSourceProxy.class);
		factory.addConstructorArgReference(element.getAttribute(DATA_SOURCE_REF));
		String weight= element.getAttribute(DATA_SOURCE_WEIGHT);
		if(!StringUtils.isEmpty(weight)){
			factory.addPropertyValue("weight", Integer.parseInt(weight));
		}
		return factory.getBeanDefinition();
	}
	
	private void parseShardingTables(Element element,ParserContext parserContext){
		BeanDefinitionBuilder globalTableFactory = BeanDefinitionBuilder.rootBeanDefinition(GlobalTableRepositorySupport.class);
		BeanDefinitionBuilder logicTableFactory = BeanDefinitionBuilder.rootBeanDefinition(LogicTableRepositorySupport.class);
		Element tablesElement= DomUtils.getChildElementByTagName(element, TABLES);
		if(tablesElement!= null){
			List<Element> globalElements= DomUtils.getChildElementsByTagName(tablesElement, GLOBAL_TABLE);
			if(!CollectionUtils.isEmpty(globalElements)){
				Set<String> globalTables= new HashSet<String>();
				for(Element it: globalElements){
					String name= it.getAttribute(TABLE_NAME);
					globalTables.add(name);
				}
				globalTableFactory.addPropertyValue("globalTables", globalTables);
			}
			List<Element> logicTableElements= DomUtils.getChildElementsByTagName(tablesElement, LOGIC_TABLE);
			ManagedList<BeanDefinition> logicTables= new ManagedList<BeanDefinition>();
			for(Element it: logicTableElements){
				logicTables.add(parseLogicTable(it,parserContext));
			}
			logicTableFactory.addPropertyValue("logicTables", logicTables);
		}
		else{
			logger.warn("GlobalTable and LogicTable are empty, Only support reading and writing separation");
		}
		//确保全局表和逻辑表的对象不为空
		globalTableRepositoryDefinition= globalTableFactory.getBeanDefinition();
		logicTableRepositoryDefinition= logicTableFactory.getBeanDefinition();
	}
	
	private BeanDefinition parseLogicTable(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LogicTableConfig.class);
		factory.addPropertyValue("name", element.getAttribute(TABLE_NAME));
		factory.addPropertyValue("primaryKey", element.getAttribute(PRIMARY_KEY));
		factory.addPropertyReference("tableStrategyConfig", element.getAttribute(TABLE_STRATEGY));
		factory.addPropertyValue("tablePostfixes", Arrays.asList(element.getAttribute(TABLE_POSTFIXES).split(",")));
		String databaseStrategy= element.getAttribute(DATA_BASE_STRATEGY);
		if(!StringUtils.isEmpty(databaseStrategy)){
			factory.addPropertyReference("databaseStrategyConfig", databaseStrategy);
		}
		List<Element> logicChildTableElements= DomUtils.getChildElementsByTagName(element, LOGIC_CHILD_TABLE);
		if(!CollectionUtils.isEmpty(logicChildTableElements)){
			ManagedList<BeanDefinition> children= new ManagedList<BeanDefinition>();
			for(Element it: logicChildTableElements){
				children.add(parseLogicChildTable(it,parserContext));
			}
			factory.addPropertyValue("children", children);
		}
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parseLogicChildTable(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LogicChildTableConfig.class);
		factory.addPropertyValue("name", element.getAttribute(TABLE_NAME));
		factory.addPropertyValue("primaryKey", element.getAttribute(PRIMARY_KEY));
		factory.addPropertyValue("foreignKey", element.getAttribute(FOREIGN_KEY));
		List<Element> logicChildTableElements= DomUtils.getChildElementsByTagName(element, LOGIC_CHILD_TABLE);
		if(!CollectionUtils.isEmpty(logicChildTableElements)){
			ManagedList<BeanDefinition> children= new ManagedList<BeanDefinition>();
			for(Element it: logicChildTableElements){
				children.add(parseLogicChildTable(it,parserContext));
			}
			factory.addPropertyValue("children", children);
		}
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parseSQLRouter(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SQLRouterSupport.class);
		String databaseRouter= element.getAttribute(DATABASE_ROUTER);
		if(!StringUtils.isEmpty(databaseRouter)){
			factory.addPropertyReference("databaseRouter", databaseRouter);
		}
		else{
			factory.addPropertyValue("databaseRouter", parseDatabaseRouter(element,parserContext));
		}
		factory.addPropertyValue("tableRouter", parseTableRouter(element,parserContext));
		return factory.getBeanDefinition();
	}
	private BeanDefinition parseDatabaseRouter(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DatabaseRouterSupport.class);
		String shardingCache= element.getAttribute(SHARDING_CACHE);
		if(!StringUtils.isEmpty(shardingCache)){
			factory.addPropertyReference("shardingCache", shardingCache);
		}
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parseTableRouter(Element element,ParserContext parserContext){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LogicTableRouterSupport.class);
		String shardingCache= element.getAttribute(SHARDING_CACHE);
		if(!StringUtils.isEmpty(shardingCache)){
			factory.addPropertyReference("shardingCache", shardingCache);
		}
		return factory.getBeanDefinition();
	}
	
	private BeanDefinition parseExecutorProcessor(){
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ExecuteProcessorSupport.class);
		return factory.getBeanDefinition();
	}
}
