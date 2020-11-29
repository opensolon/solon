package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.SessionFactory;
import org.smartboot.socket.transport.AioSession;

class _SessionFactoryImpl extends SessionFactory {
    @Override
    protected Session getSession(Object conn) {
        if (conn instanceof AioSession) {
            return _SocketSession.get((AioSession) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a AioSession type");
        }
    }

    @Override
    protected void removeSession(Object conn) {
        if (conn instanceof AioSession) {
            _SocketSession.remove((AioSession) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a socket type");
        }
    }

    @Override
    protected Session createSession(String host, int port, boolean autoReconnect) {
        AioConnector client = new AioConnector(host, port);
        client.setReadBufferSize(XPluginImp.readBufferSize);

        return new _SocketSession(client, autoReconnect);
    }
}
