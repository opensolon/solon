package org.noear.solon.boot.smartsocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;

class AioClient {
    private String host;
    private int port;
    private Protocol<Message> protocol;
    private MessageProcessor<Message> messageProcessor;
    private int readBufferSize;

    public AioClient(String host, int port, Protocol<Message> protocol, MessageProcessor<Message> messageProcessor) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.messageProcessor = messageProcessor;
    }

    public void setReadBufferSize(int size) {
        readBufferSize = size;
    }

    public AioSession start() throws IOException {
        AioQuickClient client = new AioQuickClient<>(host, port, protocol, messageProcessor);
        return client.start();
    }
}
