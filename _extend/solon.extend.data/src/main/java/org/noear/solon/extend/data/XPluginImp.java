package org.noear.solon.extend.data;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if (app.source().getAnnotation(EnableTransaction.class) != null) {
            XBridge.tranExecutorSet(TranExecutorImp.global);
        }

        if (app.source().getAnnotation(EnableCaching.class) != null) {
            XBridge.cacheServiceAddIfAbsent("", new CacheServiceDefault());
            XBridge.cacheExecutorSet(CacheExecutorImp.global);
        }
    }
}
