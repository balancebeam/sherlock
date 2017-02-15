package io.anyway.sherlock.router.strategy.support;

import io.anyway.sherlock.exception.RoutingException;
import io.anyway.sherlock.executor.ExecuteContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yangzz on 17/2/15.
 */
public abstract class AbstractEqShardingStrategy<T extends Comparable<?>> extends AbstractSingleColumnShardingStrategy<T>{

    @Override
    public Collection<String> doInSharding(ExecuteContext ctx, Collection<String> availableNames, String column, List<T> values) {
        Set<String> result= new HashSet<String>();
        for(T value: values){
            result.add(doEqualSharding(ctx,availableNames,column,value));
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(ExecuteContext ctx,Collection<String> availableNames, String column, T lower, T upper) {
        throw new RoutingException("you need implements doBetweenSharding");
    }
}
