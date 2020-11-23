package org.noear.solon.boot.smartsocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

class AioClient {
    AioQuickClient<Message> real;

    public AioClient(String host, int port, Protocol<Message> protocol, MessageProcessor<Message> messageProcessor) {
        real = new AioQuickClient<>(host, port, protocol, messageProcessor);
    }

    public void setReadBufferSize(int size) {
        real.setReadBufferSize(size);
    }

    public AioSession start() {
        try {
            return real.start();
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
