package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionConnector;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.URI;

class AioConnector implements SessionConnector<AioSession> {
    private URI uri;
    private Protocol<Message> protocol;
    private int readBufferSize;

    public AioConnector(URI uri) {
        this.uri = uri;
        this.protocol = AioProtocol.instance;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public void setReadBufferSize(int size) {
        readBufferSize = size;
    }

    @Override
    public AioSession start(Session session) throws IOException {
        AioQuickClient client = new AioQuickClient<>(uri.getHost(), uri.getPort(), protocol, new AioClientProcessor(session));
        if (readBufferSize > 0) {
            client.setReadBufferSize(readBufferSize);
        }

        return client.start();
    }
}
