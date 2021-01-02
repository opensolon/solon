package org.noear.solon.socketd.client.netty;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.socketd.SessionFactoryManager;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        SessionFactoryManager.register(new _SessionFactoryImpl());
    }
}
