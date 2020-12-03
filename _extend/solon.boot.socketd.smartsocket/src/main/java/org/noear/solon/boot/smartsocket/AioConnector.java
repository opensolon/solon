package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;

class AioConnector {
    private String host;
    private int port;
    private Protocol<Message> protocol;
    private int readBufferSize;

    public AioConnector(String host, int port) {
        this.host = host;
        this.port = port;
        this.protocol = AioProtocol.instance;
    }

    public void setReadBufferSize(int size) {
        readBufferSize = size;
    }

    public AioSession start(Session session) throws IOException {
        AioQuickClient client = new AioQuickClient<>(host, port, protocol, new AioClientProcessor(session));
        return client.start();
    }
}
