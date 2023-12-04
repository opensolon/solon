package org.noear.solon.net.websocket.socketd;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.transport.core.internal.ProcessorDefault;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 转到 Sokcet.D 协议的 WebSocketListener（服务端、客户端，都可用）
 *
 * @author noear
 * @since 2.6
 */
public class ToSocketdWebSocketListener implements WebSocketListener {
    static final String SOCKETD_KEY = "SOCKETD_KEY";

    static final Logger log = LoggerFactory.getLogger(ToSocketdWebSocketListener.class);

    private final Config config;
    private final WebSocketChannelAssistant assistant;
    private final Processor processor;

    private final InnerChannelSupporter supporter;

    public ToSocketdWebSocketListener(Config config, Listener listener) {
        this.config = config;
        this.assistant = new WebSocketChannelAssistant(config);
        this.processor = new ProcessorDefault();
        this.processor.setListener(listener);
        this.supporter = new InnerChannelSupporter(this);
    }

    private Channel getChannel(WebSocket socket) {
        Channel channel = socket.attr(SOCKETD_KEY);

        if (channel == null) {
            channel = new ChannelDefault<>(socket, supporter);
            socket.attr(SOCKETD_KEY, channel);
        }

        return channel;
    }

    @Override
    public void onOpen(WebSocket socket) {
        getChannel(socket);
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        if (log.isWarnEnabled()) {
            log.warn("Unsupported onMessage(String test)");
        }
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        try {
            Channel channel = getChannel(socket);
            Frame frame = assistant.read(binary);

            if (frame != null) {
                processor.onReceive(channel, frame);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onClose(WebSocket socket) {
        try {
            Channel channel = getChannel(socket);
            processor.onClose(channel);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        try {
            Channel channel = getChannel(socket);

            if (channel != null) {
                //有可能未 onOpen，就 onError 了；此时通道未成
                processor.onError(channel, error);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    private static class InnerChannelSupporter implements ChannelSupporter<WebSocket> {
        private ToSocketdWebSocketListener l;

        InnerChannelSupporter(ToSocketdWebSocketListener l) {
            this.l = l;
        }

        @Override
        public Processor processor() {
            return l.processor;
        }

        @Override
        public ChannelAssistant<WebSocket> assistant() {
            return l.assistant;
        }

        @Override
        public Config config() {
            return l.config;
        }
    }
}
