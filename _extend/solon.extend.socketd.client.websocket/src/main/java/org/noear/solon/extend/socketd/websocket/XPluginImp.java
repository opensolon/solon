package org.noear.solon.extend.socketd.websocket;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.socketd.SessionFactoryManager;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        SessionFactoryManager.register(new _SessionFactoryImpl());
    }
}
