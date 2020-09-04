package org.noear.solon.extend.data;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        XBridge.tranExecutorSet(TranExecutorImp.global);

        XBridge.cacheServiceAddIfAbsent("",new CacheServiceDefault());
    }
}
