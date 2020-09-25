package org.noear.solon.extend.data;

import org.noear.solon.XApp;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if (app.enableTransaction()) {
            XBridge.tranExecutorSet(TranExecutorImp.global);
        }

        if (app.enableCaching()) {
            XBridge.cacheServiceAddIfAbsent("", new CacheServiceDefault());
            XBridge.cacheExecutorSet(CacheExecutorImp.global);

            app.onEvent(BeanWrap.class, new CacheEventListener());
        }
    }
}
