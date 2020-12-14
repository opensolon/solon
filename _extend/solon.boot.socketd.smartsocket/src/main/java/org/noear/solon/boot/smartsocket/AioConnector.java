package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.Connector;
import org.noear.solon.extend.socketd.SocketProps;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.URI;

class AioConnector implements Connector<AioSession> {
    private URI uri;
    private boolean autoReconnect;
    private Protocol<Message> protocol;

    public AioConnector(URI uri, boolean autoReconnect) {
        this.uri = uri;
        this.autoReconnect = autoReconnect;
        this.protocol = AioProtocol.instance;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public boolean autoReconnect() {
        return autoReconnect;
    }

    @Override
    public Class<AioSession> realType() {
        return AioSession.class;
    }

    @Override
    public AioSession open(Session session) throws IOException {
        AioQuickClient client = new AioQuickClient<>(uri.getHost(), uri.getPort(), protocol, new AioClientProcessor(session));
        if (SocketProps.readBufferSize() > 0) {
            client.setReadBufferSize(SocketProps.readBufferSize());
        }

        if (SocketProps.writeBufferSize() > 0) {
            client.setWriteBuffer(SocketProps.writeBufferSize(), 16);
        }

        return client.start();
    }
}
