package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.extend.xsocket.MessageSessionFactory;

import java.net.Socket;

class _SessionFactoryImpl extends MessageSessionFactory {
    @Override
    protected MessageSession getSession(Object conn) {
        if (conn instanceof Socket) {
            return _SocketSession.get((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a socket type");
        }
    }

    @Override
    protected MessageSession createSession(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            return _SocketSession.get(socket);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
