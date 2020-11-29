package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Message;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;

class AioConnector {
    private String host;
    private int port;
    private Protocol<Message> protocol;
    private MessageProcessor<Message> messageProcessor;
    private int readBufferSize;

    public AioConnector(String host, int port) {
        this.host = host;
        this.port = port;
        this.protocol = AioProtocol.instance;
        this.messageProcessor = AioProcessor.instance;
    }

    public void setReadBufferSize(int size) {
        readBufferSize = size;
    }

    public AioSession start() throws IOException {
        AioQuickClient client = new AioQuickClient<>(host, port, protocol, messageProcessor);
        return client.start();
    }
}
