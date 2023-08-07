package org.noear.solon.cache.caffeine.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheLib;

/**
 * @author noear
 * @since 1.9
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CacheFactory cacheFactory = new CaffeineCacheFactoryImpl();

        CacheLib.cacheFactoryAdd("caffeine", cacheFactory);
    }
}
