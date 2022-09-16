package org.noear.solon.cache.redisson.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheLib;

/**
 * @author noear
 * @since 1.7
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CacheFactory cacheFactory = new RedissonCacheFactoryImpl();

        CacheLib.cacheFactoryAdd("redis", cacheFactory);
        CacheLib.cacheFactoryAdd("redisson", cacheFactory);
    }
}
