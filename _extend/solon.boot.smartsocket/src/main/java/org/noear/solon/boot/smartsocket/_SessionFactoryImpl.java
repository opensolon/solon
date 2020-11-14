package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.XSession;
import org.noear.solon.extend.xsocket.XSessionFactory;
import org.smartboot.socket.transport.AioSession;

import java.net.Socket;

class _SessionFactoryImpl extends XSessionFactory {
    @Override
    protected XSession getSession(Object conn) {
        if (conn instanceof AioSession) {
            return _SocketSession.get((AioSession) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a AioSession type");
        }
    }
}
