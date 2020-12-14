package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.Connector;
import org.noear.solon.extend.socketd.SessionFactory;
import org.smartboot.socket.transport.AioSession;

import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"tcp"};
    }

    @Override
    public Class<?> driveType() {
        return AioSession.class;
    }

    @Override
    public Session createSession(Connector connector) {
        if (connector.driveType() == AioSession.class) {
            return new _SocketSession((Connector<AioSession>) connector);
        } else {
            throw new IllegalArgumentException("Only support Connector<AioSession> connector");
        }
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        AioConnector client = new AioConnector(uri, autoReconnect);

        return new _SocketSession(client);
    }
}
