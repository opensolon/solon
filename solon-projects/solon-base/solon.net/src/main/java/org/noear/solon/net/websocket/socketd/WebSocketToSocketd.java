package org.noear.solon.net.websocket.socketd;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.transport.core.internal.ProcessorDefault;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 将 WebSocket 转为 Sokcet.D 协议
 *
 * @author noear
 * @since 2.6
 */
public class WebSocketToSocketd implements WebSocketListener {

    static final Logger log = LoggerFactory.getLogger(WebSocketToSocketd.class);

    private final ServerConfig config;
    private final WebSocketChannelAssistant assistant;
    private final Processor processor;

    public WebSocketToSocketd() {
        config = new ServerConfig("ws");
        assistant = new WebSocketChannelAssistant(config);
        processor = new ProcessorDefault();
    }


    public WebSocketToSocketd listener(Listener listener) {
        processor.setListener(listener);
        return this;
    }

    private Channel getChannel(WebSocket socket) {
        Channel channel = socket.getAttachment();

        if (channel == null) {
            channel = new ChannelDefault<>(socket, config, assistant);
            socket.setAttachment(channel);
        }

        return channel;
    }

    @Override
    public void onOpen(WebSocket socket) {
        Channel channel = getChannel(socket);
        socket.setAttachment(channel);
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
}
