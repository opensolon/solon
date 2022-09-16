package org.noear.solon.cache.caffeine.integration;

import org.noear.solon.cache.caffeine.CaffeineCacheService;
import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheService;

import java.util.Properties;

/**
 * @author noear
 * @since 1.9
 */
public class CaffeineCacheFactoryImpl implements CacheFactory {
    @Override
    public CacheService create(Properties props) {
        return new CaffeineCacheService(props);
    }
}
