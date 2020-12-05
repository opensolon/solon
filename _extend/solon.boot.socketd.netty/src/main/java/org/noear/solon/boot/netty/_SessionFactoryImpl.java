package org.noear.solon.boot.netty;

import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;

import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"tpc"};
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        NioConnector connector = new NioConnector(uri);

        return new _SocketSession(connector, autoReconnect);
    }
}
