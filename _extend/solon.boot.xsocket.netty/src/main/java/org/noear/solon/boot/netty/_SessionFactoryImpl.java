package org.noear.solon.boot.netty;

import io.netty.channel.Channel;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.SessionFactory;

class _SessionFactoryImpl extends SessionFactory {
    @Override
    protected Session getSession(Object conn) {
        if (conn instanceof Channel) {
            return _SocketSession.get((Channel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a AioSession type");
        }
    }

    @Override
    protected void removeSession(Object conn) {
        if (conn instanceof Channel) {
            _SocketSession.remove((Channel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a socket type");
        }
    }

    @Override
    protected Session createSession(String host, int port, boolean autoReconnect) {
        NioConnector connector = new NioConnector(host, port);

        return new _SocketSession(connector, autoReconnect);
    }
}
