package org.noear.solon.cache.redisson.integration;

import org.noear.solon.cache.redisson.RedissonCacheService;
import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheService;

import java.util.Properties;

/**
 * @author noear
 * @since 1.7
 */
class RedissonCacheFactoryImpl implements CacheFactory {
    @Override
    public CacheService create(Properties props) {
        return new RedissonCacheService(props);
    }
}
