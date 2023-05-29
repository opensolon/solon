package org.noear.solon.cache.spymemcached.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.data.cache.CacheLib;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CacheLib.cacheFactoryAdd("memcached", new MemCacheFactoryImpl());
    }
}
