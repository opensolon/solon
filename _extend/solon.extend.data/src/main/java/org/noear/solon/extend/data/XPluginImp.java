package org.noear.solon.extend.data;

import org.noear.solon.Solon;
import org.noear.solon.core.*;
import org.noear.solon.core.cache.CacheService;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        if (app.enableTransaction()) {
            Bridge.tranExecutorSet(TranExecutorImp.global);
        }

        if (app.enableCaching()) {
            CacheLib.cacheServiceAddIfAbsent("", CacheServiceDefault.instance);

            app.onEvent(BeanWrap.class, new CacheEventListener());
        }

        Aop.context().beanOnloaded(() -> {
            if (Aop.has(CacheService.class) == false) {
                Aop.wrapAndPut(CacheService.class, CacheServiceDefault.instance);
            }
        });
    }
}
