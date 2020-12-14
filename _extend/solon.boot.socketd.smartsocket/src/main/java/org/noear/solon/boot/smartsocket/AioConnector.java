package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ConnectorSimple;
import org.noear.solon.extend.socketd.SocketProps;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.URI;

class AioConnector extends ConnectorSimple<AioSession> {

    public AioConnector(URI uri, boolean autoReconnect) {
        super(uri, autoReconnect);
    }

    @Override
    public Class<AioSession> driveType() {
        return AioSession.class;
    }

    @Override
    public AioSession open(Session session) throws IOException {
        AioQuickClient client = new AioQuickClient<>(uri().getHost(), uri().getPort(), AioProtocol.instance, new AioClientProcessor(session));
        if (SocketProps.readBufferSize() > 0) {
            client.setReadBufferSize(SocketProps.readBufferSize());
        }

        if (SocketProps.writeBufferSize() > 0) {
            client.setWriteBuffer(SocketProps.writeBufferSize(), 16);
        }

        return client.start();
    }
}
