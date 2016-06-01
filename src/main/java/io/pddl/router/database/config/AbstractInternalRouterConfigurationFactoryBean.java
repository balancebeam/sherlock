package io.pddl.router.database.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;

import io.pddl.router.database.DatabaseRouter;
import io.pddl.router.database.rule.RoutingRule;
import io.pddl.router.database.support.IBatisRoutingFact;
import io.pddl.router.database.support.DefaultDatabaseRouter;
import io.pddl.router.table.GlobalTableRepository;

public abstract class AbstractInternalRouterConfigurationFactoryBean implements FactoryBean<DatabaseRouter<?>>, ApplicationContextAware,InitializingBean {

	private DefaultDatabaseRouter dbRouter;
	
	private boolean enableCache;
    private Resource configLocation;
    private Resource[] configLocations;
    
    private ApplicationContext ctx;
    
    private Map<String,Object> functionsMap = new HashMap<String, Object>();

	@Override
	public DefaultDatabaseRouter getObject() throws Exception {
		return dbRouter;
	}

	@Override
	public Class<?> getObjectType() {
		return DatabaseRouter.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    public Resource getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocations(Resource[] configLocations) {
        this.configLocations = configLocations;
    }

    public Resource[] getConfigLocations() {
        return configLocations;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    public boolean isEnableCache() {
        return enableCache;
    }
    
    public void setFunctionsMap(Map<String, Object> functionMaps) {
        if (functionMaps == null) {
            return;
        }
        this.functionsMap = functionMaps;
    }

    public Map<String, Object> getFunctionsMap() {
        return functionsMap;
    }
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.dbRouter= new DefaultDatabaseRouter(false);
		
		final Set<RoutingRule<IBatisRoutingFact, List<String>>> sqlActionShardingRules = new HashSet<RoutingRule<IBatisRoutingFact, List<String>>>();
        final Set<RoutingRule<IBatisRoutingFact, List<String>>> sqlActionRules = new HashSet<RoutingRule<IBatisRoutingFact, List<String>>>();
        final Set<RoutingRule<IBatisRoutingFact, List<String>>> namespaceShardingRules = new HashSet<RoutingRule<IBatisRoutingFact, List<String>>>();
        final Set<RoutingRule<IBatisRoutingFact, List<String>>> namespaceRules = new HashSet<RoutingRule<IBatisRoutingFact, List<String>>>();

        if (getConfigLocation() != null) {
            assembleRulesForRouter(this.dbRouter, getConfigLocation(), sqlActionShardingRules,
                    sqlActionRules, namespaceShardingRules, namespaceRules);
        }

        if (!ObjectUtils.isEmpty(getConfigLocations())) {
            for (Resource res : getConfigLocations()) {
                assembleRulesForRouter(this.dbRouter, res, sqlActionShardingRules, sqlActionRules,
                        namespaceShardingRules, namespaceRules);
            }
        }

        List<Set<RoutingRule<IBatisRoutingFact, List<String>>>> ruleSequences = new ArrayList<Set<RoutingRule<IBatisRoutingFact, List<String>>>>() {
            private static final long serialVersionUID = 1493353938640646578L;
            {
                add(sqlActionShardingRules);
                add(sqlActionRules);
                add(namespaceShardingRules);
                add(namespaceRules);
            }
        };
        dbRouter.setGlobalTableRepository(ctx.getBean(GlobalTableRepository.class));
        dbRouter.setRuleSequences(ruleSequences);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx){
		this.ctx= ctx;
	}
	
	protected abstract void assembleRulesForRouter(
			DatabaseRouter<?> router,
            Resource configLocation,
            Set<RoutingRule<IBatisRoutingFact, List<String>>> sqlActionShardingRules,
            Set<RoutingRule<IBatisRoutingFact, List<String>>> sqlActionRules,
            Set<RoutingRule<IBatisRoutingFact, List<String>>> namespaceShardingRules,
            Set<RoutingRule<IBatisRoutingFact, List<String>>> namespaceRules)
            		throws IOException;
}
