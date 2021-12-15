package org.noear.solon.cache.jedis.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.data.cache.CacheLib;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        CacheLib.cacheFactoryAdd("redis", new RedisCacheFactoryImpl());
    }
}
