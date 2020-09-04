package org.noear.solon.extend.data;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XPlugin;
import org.noear.solon.extend.data.annotation.EnableCaching;
import org.noear.solon.extend.data.annotation.EnableTransaction;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        EnableTransaction enableTransaction = app.source().getAnnotation(EnableTransaction.class);
        EnableCaching enableCaching = app.source().getAnnotation(EnableCaching.class);

        if (enableTransaction == null || enableTransaction.value()) {
            XBridge.tranExecutorSet(TranExecutorImp.global);
        }

        if (enableCaching == null || enableCaching.value()) {
            XBridge.cacheServiceAddIfAbsent("", new CacheServiceDefault());
            XBridge.cacheExecutorSet(CacheExecutorImp.global);
        }
    }
}
