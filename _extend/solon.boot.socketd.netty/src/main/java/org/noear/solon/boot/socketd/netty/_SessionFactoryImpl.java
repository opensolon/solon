package org.noear.solon.boot.socketd.netty;

import io.netty.channel.Channel;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.Connector;
import org.noear.solon.extend.socketd.SessionFactory;

import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"tcp"};
    }

    @Override
    public Class<?> driveType() {
        return Channel.class;
    }

    @Override
    public Session createSession(Connector connector) {
        if (connector.driveType() == Channel.class) {
            return new _SocketSession((Connector<Channel>) connector);
        } else {
            throw new IllegalArgumentException("Only support Connector<Channel> connector");
        }
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        NioConnector connector = new NioConnector(uri, autoReconnect);

        return new _SocketSession(connector);
    }
}
