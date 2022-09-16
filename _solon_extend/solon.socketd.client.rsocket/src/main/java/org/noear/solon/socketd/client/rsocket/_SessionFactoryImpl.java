package org.noear.solon.socketd.client.rsocket;

import io.rsocket.RSocket;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.Connector;
import org.noear.solon.socketd.SessionFactory;

import java.net.URI;

/**
 * @author noear 2020/12/14 created
 * @since 1.2
 */
class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"tcp","rtcp"};
    }

    @Override
    public Class<?> driveType() {
        return RSocket.class;
    }

    @Override
    public Session createSession(Connector connector) {
        if (connector.driveType() == RSocket.class) {
            return new RsSocketSession((Connector<RSocket>) connector);
        } else {
            throw new IllegalArgumentException("Only support Connector<RSocket> connector");
        }
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        return new RsSocketSession(new RsConnector(uri, autoReconnect));
    }
}
