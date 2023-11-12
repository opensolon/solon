package org.noear.nami.channel.socketd;

import org.noear.nami.NamiManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 * @since 2.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        NamiManager.reg("tcp", SocketdClientChannel.instance);
        NamiManager.reg("udp", SocketdClientChannel.instance);
        NamiManager.reg("ws", SocketdClientChannel.instance);
        NamiManager.reg("wss", SocketdClientChannel.instance);
    }
}
