package org.noear.solon.cache.jedis.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheLib;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        CacheFactory cacheFactory = new RedisCacheFactoryImpl();

        CacheLib.cacheFactoryAdd("redis", cacheFactory);
        CacheLib.cacheFactoryAdd("jedis", cacheFactory);
    }
}
