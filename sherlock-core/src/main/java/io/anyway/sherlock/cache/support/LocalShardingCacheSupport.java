package io.anyway.sherlock.cache.support;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import io.anyway.sherlock.cache.ShardingCache;

public class LocalShardingCacheSupport implements ShardingCache{

    private Map<Object,Object> cache= new HashMap<Object,Object>();

    @Override
    public String getLogicTablePostfix(String tableName, String primaryKey, Comparable<?> value) {
    	HashCode hashCode =  Hashing.md5().newHasher()
    			.putString(tableName, Charsets.UTF_8)
    			.putString(primaryKey, Charsets.UTF_8)
    			.putString(String.valueOf(value), Charsets.UTF_8)
    			.hash();
        return (String) cache.get(hashCode);
    }

    @Override
    public void putLocalTablePostfix(String tableName, String primaryKey, Comparable<?> value, String postfix) {
    	HashCode hashCode =  Hashing.md5().newHasher()
    			.putString(tableName, Charsets.UTF_8)
    			.putString(primaryKey, Charsets.UTF_8)
    			.putString(String.valueOf(value), Charsets.UTF_8)
    			.hash();
        cache.put(hashCode,postfix);
    }

}
