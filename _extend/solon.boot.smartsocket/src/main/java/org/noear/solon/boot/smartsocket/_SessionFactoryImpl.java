package org.noear.solon.boot.smartsocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.extend.xsocket.XSessionFactory;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

class _SessionFactoryImpl extends XSessionFactory {
    @Override
    protected MessageSession getSession(Object conn) {
        if (conn instanceof AioSession) {
            return _SocketSession.get((AioSession) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a AioSession type");
        }
    }

    @Override
    protected MessageSession createSession(String host, int port) {
        AioQuickClient<Message> client = new AioQuickClient<>(host, port, new AioProtocol(), new AioProcessor());
        client.setReadBufferSize(XPluginImp.readBufferSize);

        try {
            AioSession conn = client.start();

            return _SocketSession.get(conn);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
