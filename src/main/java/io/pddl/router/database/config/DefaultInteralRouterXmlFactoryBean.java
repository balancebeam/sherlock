package io.pddl.router.database.config;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.core.io.Resource;

import com.thoughtworks.xstream.XStream;

import io.pddl.router.database.DatabaseRouter;
import io.pddl.router.database.config.vo.InternalRule;
import io.pddl.router.database.config.vo.InternalRules;
import io.pddl.router.database.rule.RoutingRule;
import io.pddl.router.database.rule.ibatis.IBatisNamespaceRule;
import io.pddl.router.database.rule.ibatis.IBatisNamespaceShardingRule;
import io.pddl.router.database.rule.ibatis.IBatisSqlActionRule;
import io.pddl.router.database.rule.ibatis.IBatisSqlActionShardingRule;
import io.pddl.router.database.support.IBatisRoutingFact;
import io.pddl.util.CollectionUtils;
import io.pddl.util.MapUtils;

public class DefaultInteralRouterXmlFactoryBean extends AbstractInternalRouterConfigurationFactoryBean{

	@Override
	protected void assembleRulesForRouter(DatabaseRouter<?> router, Resource configLocation,
			Set<RoutingRule<IBatisRoutingFact, List<String>>> sqlActionShardingRules,
			Set<RoutingRule<IBatisRoutingFact, List<String>>> sqlActionRules,
			Set<RoutingRule<IBatisRoutingFact, List<String>>> namespaceShardingRules,
			Set<RoutingRule<IBatisRoutingFact, List<String>>> namespaceRules) throws IOException {
		
		XStream xstream = new XStream();
        xstream.alias("rules", InternalRules.class);
        xstream.alias("rule", InternalRule.class);
        xstream.addImplicitCollection(InternalRules.class, "rules");
        xstream.useAttributeFor(InternalRule.class, "merger");

        InternalRules internalRules = (InternalRules) xstream.fromXML(configLocation.getInputStream());
        List<InternalRule> rules = internalRules.getRules();
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }

        for (InternalRule rule : rules) {
            String namespace = StringUtils.trimToEmpty(rule.getNamespace());
            String sqlAction = StringUtils.trimToEmpty(rule.getSqlmap());
            String shardingExpression = StringUtils.trimToEmpty(rule.getShardingExpression());
            String destinations = StringUtils.trimToEmpty(rule.getShards());

            Validate.notEmpty(destinations, "destination shards must be given explicitly.");

            if (StringUtils.isEmpty(namespace) && StringUtils.isEmpty(sqlAction)) {
                throw new IllegalArgumentException(
                        "at least one of 'namespace' or 'sqlAction' must be given.");
            }
            if (StringUtils.isNotEmpty(namespace) && StringUtils.isNotEmpty(sqlAction)) {
                throw new IllegalArgumentException(
                        "'namespace' and 'sqlAction' are alternatives, can't guess which one to use if both of them are provided.");
            }

            if (StringUtils.isNotEmpty(namespace)) {
                if (StringUtils.isEmpty(shardingExpression)) {
                    namespaceRules.add(new IBatisNamespaceRule(namespace, destinations));
                } else {
                    IBatisNamespaceShardingRule insr = new IBatisNamespaceShardingRule(namespace,
                            destinations, shardingExpression);
                    if (MapUtils.isNotEmpty(getFunctionsMap())) {
                        insr.setFunctionMap(getFunctionsMap());
                    }
                    namespaceShardingRules.add(insr);
                }
            }
            if (StringUtils.isNotEmpty(sqlAction)) {
                if (StringUtils.isEmpty(shardingExpression)) {
                    sqlActionRules.add(new IBatisSqlActionRule(sqlAction, destinations));
                } else {
                    IBatisSqlActionShardingRule issr = new IBatisSqlActionShardingRule(sqlAction,
                            destinations, shardingExpression);
                    if (MapUtils.isNotEmpty(getFunctionsMap())) {
                        issr.setFunctionMap(getFunctionsMap());
                    }
                    sqlActionShardingRules.add(issr);
                }
            }
        }
		
	}

}
