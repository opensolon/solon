package org.noear.solon.cache.spymemcached.integration;

import org.noear.solon.cache.spymemcached.MemCacheService;
import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheService;

import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
class MemCacheFactoryImpl implements CacheFactory {
    @Override
    public CacheService create(Properties props) {
        return new MemCacheService(props);
    }
}
