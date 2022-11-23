package org.noear.solon.data.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.*;
import org.noear.solon.data.annotation.*;
import org.noear.solon.data.cache.*;
import org.noear.solon.data.datasource.annotation.TargetDataSource;
import org.noear.solon.data.datasource.annotation.TargetDataSourceInterceptor;
import org.noear.solon.data.tran.RollbackInterceptor;
import org.noear.solon.data.tran.TranExecutor;
import org.noear.solon.data.around.CacheInterceptor;
import org.noear.solon.data.around.CachePutInterceptor;
import org.noear.solon.data.around.CacheRemoveInterceptor;
import org.noear.solon.data.around.TranInterceptor;
import org.noear.solon.data.tran.TranExecutorImp;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //注册缓存工厂
        CacheLib.cacheFactoryAdd("local", new LocalCacheFactoryImpl());

        //多数据源切换
        context.beanAroundAdd(TargetDataSource.class, new TargetDataSourceInterceptor());

        //添加事务控制支持
        if (Solon.app().enableTransaction()) {
            context.wrapAndPut(TranExecutor.class, TranExecutorImp.global);

            context.beanAroundAdd(Tran.class, new TranInterceptor(), 120);

            context.beanAroundAdd(Rollback.class, new RollbackInterceptor(), 120);
        }

        //添加缓存控制支持
        if (Solon.app().enableCaching()) {
            CacheLib.cacheServiceAddIfAbsent("", LocalCacheService.instance);

            context.subWrapsOfType(CacheService.class, new CacheServiceWrapConsumer());

            context.beanOnloaded((ctx) -> {
                if (ctx.hasWrap(CacheService.class) == false) {
                    ctx.wrapAndPut(CacheService.class, LocalCacheService.instance);
                }
            });

            context.beanAroundAdd(CachePut.class, new CachePutInterceptor(), 110);
            context.beanAroundAdd(CacheRemove.class, new CacheRemoveInterceptor(), 110);
            context.beanAroundAdd(Cache.class, new CacheInterceptor(), 111);
        }
    }
}
