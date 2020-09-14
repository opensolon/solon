package org.noear.solon.extend.data;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.CacheService;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if (app.enableTransaction) {
            XBridge.tranExecutorSet(TranExecutorImp.global);
        }

        if (app.enableCaching) {
            XBridge.cacheServiceAddIfAbsent("", new CacheServiceDefault());
            XBridge.cacheExecutorSet(CacheExecutorImp.global);
        }

        /**
         * 通过容器获取并注册CacheService
         * */
        Aop.getAsyn(CacheService.class, (bw) -> {
            XBridge.cacheServiceAdd("", bw.raw());
        });

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, bw) -> {
                if (bw.raw() instanceof CacheService) {
                    XBridge.cacheServiceAddIfAbsent(k, bw.raw());
                }
            });
        });
    }
}
