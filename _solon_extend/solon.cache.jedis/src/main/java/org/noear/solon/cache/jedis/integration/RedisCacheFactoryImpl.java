package org.noear.solon.cache.jedis.integration;

import org.noear.solon.cache.jedis.RedisCacheService;
import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheService;

import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
class RedisCacheFactoryImpl implements CacheFactory {
    @Override
    public CacheService create(Properties props) {
        return new RedisCacheService(props);
    }
}
