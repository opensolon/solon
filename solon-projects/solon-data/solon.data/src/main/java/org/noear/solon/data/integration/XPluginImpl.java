package org.noear.solon.data.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.*;
import org.noear.solon.data.annotation.*;
import org.noear.solon.data.cache.*;
import org.noear.solon.data.cache.interceptor.CacheInterceptor;
import org.noear.solon.data.cache.interceptor.CachePutInterceptor;
import org.noear.solon.data.cache.interceptor.CacheRemoveInterceptor;
import org.noear.solon.data.tran.interceptor.TranInterceptor;

public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        //注册缓存工厂
        CacheLib.cacheFactoryAdd("local", new LocalCacheFactoryImpl());

        //添加事务控制支持
        if (Solon.app().enableTransaction()) {
            context.beanInterceptorAdd(Tran.class, TranInterceptor.instance, 120);
        }

        //添加缓存控制支持
        if (Solon.app().enableCaching()) {
            CacheLib.cacheServiceAddIfAbsent("", LocalCacheService.instance);

            context.subWrapsOfType(CacheService.class, new CacheServiceWrapConsumer());

            context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
                if (context.hasWrap(CacheService.class) == false) {
                    context.wrapAndPut(CacheService.class, LocalCacheService.instance);
                }
            });

            context.beanInterceptorAdd(CachePut.class, new CachePutInterceptor(), 110);
            context.beanInterceptorAdd(CacheRemove.class, new CacheRemoveInterceptor(), 110);
            context.beanInterceptorAdd(Cache.class, new CacheInterceptor(), 111);
        }
    }
}
