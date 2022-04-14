package org.noear.solon.cache.redisson;

import org.noear.solon.data.cache.CacheService;

import java.util.Properties;

/**
 * @author noear
 * @since 1.7
 */
public class RedissonCacheService implements CacheService {
    public RedissonCacheService(Properties props){

    }

    @Override
    public void store(String key, Object obj, int seconds) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void remove(String key) {

    }
}
