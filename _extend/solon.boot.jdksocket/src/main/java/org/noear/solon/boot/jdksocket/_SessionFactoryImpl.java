package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.XSession;
import org.noear.solon.extend.xsocket.XSessionFactory;

import java.net.Socket;

class _SessionFactoryImpl extends XSessionFactory {
    @Override
    protected XSession getSession(Object conn) {
        if (conn instanceof Socket) {
            return _SocketSession.get((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a socket type");
        }
    }
}
