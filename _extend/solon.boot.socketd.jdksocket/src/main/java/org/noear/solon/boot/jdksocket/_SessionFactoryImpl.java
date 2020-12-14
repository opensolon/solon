package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.Connector;
import org.noear.solon.extend.socketd.SessionFactory;

import java.net.Socket;
import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"tcp"};
    }

    @Override
    public Class<?> driveType() {
        return Socket.class;
    }

    @Override
    public Session createSession(Connector connector) {
        if (connector.driveType() == Socket.class) {
            return new _SocketSession((Connector<Socket>) connector);
        } else {
            throw new IllegalArgumentException("Only support Connector<Socket> connector");
        }
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        try {
            BioConnector bioClient = new BioConnector(uri, autoReconnect);

            return new _SocketSession(bioClient);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
