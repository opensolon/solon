package org.noear.solon.socketd.client.smartsocket;

import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.socketd.SessionFactoryManager;

public final class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //注册会话工厂
        SessionFactoryManager.register(new _SessionFactoryImpl());
    }
}
