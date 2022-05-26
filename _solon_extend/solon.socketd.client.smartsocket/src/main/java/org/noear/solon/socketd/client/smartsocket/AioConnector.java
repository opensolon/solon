package org.noear.solon.socketd.client.smartsocket;

import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ConnectorBase;
import org.noear.solon.socketd.SocketProps;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.URI;

class AioConnector extends ConnectorBase<AioSession> {

    public AioConnector(URI uri, boolean autoReconnect) {
        super(uri, autoReconnect);
    }

    @Override
    public Class<AioSession> driveType() {
        return AioSession.class;
    }

    @Override
    public AioSession open(Session session) throws IOException {
        AioQuickClient client = new AioQuickClient(uri().getHost(), uri().getPort(), AioProtocol.instance, new AioClientProcessor(session));
        if (SocketProps.readBufferSize() > 0) {
            client.setReadBufferSize(SocketProps.readBufferSize());
        }

        if (SocketProps.writeBufferSize() > 0) {
            client.setWriteBuffer(SocketProps.writeBufferSize(), 16);
        }

        if (SocketProps.connectTimeout() > 0) {
            client.connectTimeout(SocketProps.connectTimeout());
        }

        return client.start();
    }
}
