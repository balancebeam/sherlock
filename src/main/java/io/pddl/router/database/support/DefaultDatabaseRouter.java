package io.pddl.router.database.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.mapping.sql.stat.StaticSql;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;

import io.pddl.cache.ShardingCache;
import io.pddl.exception.RoutingException;
import io.pddl.router.database.DatabaseRouter;
import io.pddl.router.database.rule.RoutingRule;
import io.pddl.router.table.GlobalTableRepository;
import io.pddl.sqlparser.SQLParserFactory;

public class DefaultDatabaseRouter implements DatabaseRouter<IBatisRoutingFact> {

	private transient final Logger logger = LoggerFactory.getLogger(DefaultDatabaseRouter.class);
	
	private GlobalTableRepository globalTableRepository;

	private boolean enableCache = false;

	private ShardingCache shardingCache;

	private List<Set<RoutingRule<IBatisRoutingFact, List<String>>>> ruleSequences = new ArrayList<Set<RoutingRule<IBatisRoutingFact, List<String>>>>();

	public DefaultDatabaseRouter(boolean enableCache) {
		this.enableCache = enableCache;
	}

	public boolean isEnableCache() {
		return enableCache;
	}
	
	public void setGlobalTableRepository(GlobalTableRepository globalTableRepository){
		this.globalTableRepository= globalTableRepository;
	}

	public void setShardingCache(ShardingCache shardingCache) {
		this.shardingCache = shardingCache;
	}

	public void setRuleSequences(List<Set<RoutingRule<IBatisRoutingFact, List<String>>>> ruleSequences) {
		this.ruleSequences = ruleSequences;
	}

	public List<Set<RoutingRule<IBatisRoutingFact, List<String>>>> getRuleSequences() {
		return ruleSequences;
	}

	@Override
	public RoutingResult doRoute(IBatisRoutingFact routingFact) throws RoutingException {
		if (enableCache && shardingCache != null) {
			synchronized (this) {
				RoutingResult result = shardingCache.getDatabaseRoutingResult(routingFact);
				if (result != null) {
					logger.info("return routing result:{} from cache for fact:{}", result, routingFact);
					return result;
				}
			}
		}

		RoutingResult result = new RoutingResult();
		result.setResourceIdentities(new ArrayList<String>());

		RoutingRule<IBatisRoutingFact, List<String>> ruleToUse = null;
		if (!CollectionUtils.isEmpty(getRuleSequences())) {
			for (Set<RoutingRule<IBatisRoutingFact, List<String>>> ruleSet : getRuleSequences()) {
				ruleToUse = searchMatchedRuleAgainst(ruleSet, routingFact);
				if (ruleToUse != null) {
					break;
				}
			}
		}

		if (ruleToUse != null) {
			logger.info("matched with rule:{} with fact:{}", ruleToUse, routingFact);
			result.getResourceIdentities().addAll(ruleToUse.action());
		} else {
			logger.info("No matched rule found for routing fact:{}", routingFact);
		}

		if (enableCache && shardingCache != null) {
			synchronized (this) {
				shardingCache.putDatabaseRoutingResult(routingFact, result);
			}
		}

		return result;
	}

	private RoutingRule<IBatisRoutingFact, List<String>> searchMatchedRuleAgainst(
		Set<RoutingRule<IBatisRoutingFact, List<String>>> rules, IBatisRoutingFact routingFact) {
		if (CollectionUtils.isEmpty(rules)) {
			return null;
		}
		for (RoutingRule<IBatisRoutingFact, List<String>> rule : rules) {
			if (rule.isDefinedAt(routingFact)) {
				return rule;
			}
		}
		return null;
	}

	@Override
	public RoutingResult doGlobalTableRoute(IBatisRoutingFact routingFact) throws RoutingException {
		if(globalTableRepository==null || globalTableRepository.isGlobalTableEmpty()){
			return null;
		}
		
		if (enableCache && shardingCache!=null) {
			RoutingResult result= shardingCache.getDatabaseRoutingResultByGlobalTable(routingFact);
			if(result!= null){
				return result;
			}
		}
		
		SqlMapClientImpl sqlMapClient= (SqlMapClientImpl)routingFact.getSqlMapClient();
		MappedStatement mstate= sqlMapClient.getMappedStatement(routingFact.getAction());
		String sql= "";
		RoutingResult result= null;
		if (mstate.getSql() instanceof StaticSql) {
			sql= mstate.getSql().getSql(null, routingFact.getArgument());
        } else {
        	StatementScope scope= new StatementScope(new SessionScope());
        	scope.setStatement(mstate);
        	sql= mstate.getSql().getSql(scope, routingFact.getArgument());
        }
		
		String tableName= SQLParserFactory.getDMLTableName(sql);
		
		if(tableName!= null){
			List<String> dbNames= (List<String>)globalTableRepository.getDBPartitionNames(tableName);
			if(!CollectionUtils.isEmpty(dbNames)){
				result = new RoutingResult();
				result.setResourceIdentities(dbNames);
			}
		}
		
		if (enableCache && shardingCache!=null) {
			synchronized (this) {
				shardingCache.putDatabaseRoutingResultByGlobalTable(routingFact, result);
	        }
		}
		return result;
	}

}
