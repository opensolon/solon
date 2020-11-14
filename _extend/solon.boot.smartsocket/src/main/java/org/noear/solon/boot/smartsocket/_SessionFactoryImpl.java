package org.noear.solon.boot.smartsocket;

import org.noear.solon.XUtil;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;
import org.noear.solon.extend.xsocket.XSessionFactory;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

class _SessionFactoryImpl extends XSessionFactory {
    @Override
    protected XSession getSession(Object conn) {
        if (conn instanceof AioSession) {
            return _SocketSession.get((AioSession) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a AioSession type");
        }
    }

    @Override
    protected XSession createSession(String host, int port) {
        AioQuickClient<XMessage> client = new AioQuickClient<>(host, port, new AioProtocol(), new AioProcessor());
        client.setReadBufferSize(1024 * 1024 * 2);

        try {
            AioSession conn = client.start();

            return _SocketSession.get(conn);
        } catch (Exception ex) {
            throw XUtil.throwableWrap(ex);
        }
    }
}
