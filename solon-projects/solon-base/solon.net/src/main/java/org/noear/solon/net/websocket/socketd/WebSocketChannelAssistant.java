package org.noear.solon.net.websocket.socketd;

import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.buffer.ByteBufferReader;
import org.noear.socketd.transport.core.buffer.ByteBufferWriter;
import org.noear.solon.net.websocket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketChannelAssistant implements ChannelAssistant<WebSocket> {
    private final Config config;

    public WebSocketChannelAssistant(Config config) {
        this.config = config;
    }
    @Override
    public void write(WebSocket target, Frame frame) throws IOException {
        ByteBufferWriter writer = config.getCodec().write(frame, len -> new ByteBufferWriter(ByteBuffer.allocate(len)));
        target.send(writer.getBuffer());
    }

    public Frame read(ByteBuffer buffer) throws IOException{
        return config.getCodec().read(new ByteBufferReader(buffer));
    }

    @Override
    public boolean isValid(WebSocket target) {
        return target.isValid();
    }

    @Override
    public void close(WebSocket target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(WebSocket target) throws IOException {
        return target.remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(WebSocket target) throws IOException {
        return target.localAddress();
    }
}
