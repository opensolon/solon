package org.noear.nami.channel.socketd;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/1/3 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        NamiManager.reg("tcp", SocketClientChannel.instance);
        NamiManager.reg("ws", SocketClientChannel.instance);
        NamiManager.reg("wss", SocketClientChannel.instance);
    }
}
