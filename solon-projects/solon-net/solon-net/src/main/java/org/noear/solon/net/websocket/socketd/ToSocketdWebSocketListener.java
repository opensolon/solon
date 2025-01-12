/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.net.websocket.socketd;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.ByteBufferCodecWriter;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.core.impl.ProcessorDefault;
import org.noear.socketd.utils.IoCompletionHandler;
import org.noear.solon.Utils;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * 转到 Sokcet.D 协议的 WebSocketListener（服务端、客户端，都可用）
 *
 * @author noear
 * @since 2.6
 */
public class ToSocketdWebSocketListener implements WebSocketListener, SubProtocolCapable {
    static final String SOCKETD_KEY = "SOCKETD_KEY";

    static final Logger log = LoggerFactory.getLogger(ToSocketdWebSocketListener.class);

    private final Config config;
    private final InnerChannelAssistant assistant;
    private final Processor processor;
    private final InnerListenerWrapper listenerWrapper;

    private final InnerChannelSupporter supporter;

    public ToSocketdWebSocketListener(Config config) {
        this(config, null);
    }

    public ToSocketdWebSocketListener(Config config, Listener listener) {
        this.config = config;
        this.assistant = new InnerChannelAssistant(config);
        this.listenerWrapper = new InnerListenerWrapper();
        this.processor = new ProcessorDefault();
        this.processor.setListener(listenerWrapper);
        this.supporter = new InnerChannelSupporter(this);

        if (listener == null) {
            if (this instanceof Listener) {
                setListener((Listener) this);
            }
        } else {
            setListener(listener);
        }
    }

    /**
     * 设置 Socket.D 监听器
     */
    public void setListener(Listener listener) {
        this.listenerWrapper.setListener(listener);
    }

    /**
     * 获取 通道
     */
    private ChannelInternal getChannel(WebSocket socket) {
        ChannelInternal channel = socket.attr(SOCKETD_KEY);

        if (channel == null) {
            channel = new ChannelDefault<>(socket, supporter);
            socket.attr(SOCKETD_KEY, channel);
        }

        return channel;
    }

    @Override
    public String getSubProtocols(Collection<String> requestProtocols) {
        //子协议验证
        if (config.isUseSubprotocols()) {
            return SocketD.protocolName();
        } else {
            if (Utils.isEmpty(requestProtocols)) {
                return null;
            } else {
                return SocketD.protocolName();
            }
        }
    }

    @Override
    public void onOpen(WebSocket socket) {
        ChannelInternal channel = getChannel(socket);

        //头信息
        MultiMap<String> headerMap = socket.paramMap();
        channel.getSession().attrPut(InnerListenerWrapper.WS_HANDSHAKE_HEADER, headerMap);
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
            ChannelInternal channel = getChannel(socket);
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
            ChannelInternal channel = getChannel(socket);
            processor.onClose(channel);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        try {
            ChannelInternal channel = getChannel(socket);

            if (channel != null) {
                //有可能未 onOpen，就 onError 了；此时通道未成
                processor.onError(channel, error);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onPing(WebSocket socket) {
        assertHandshake(socket);
    }

    @Override
    public void onPong(WebSocket socket) {
        assertHandshake(socket);
    }

    /**
     * 禁止 ws 客户端连接 sd:ws 服务（避免因为 ws 心跳，又不会触发空闲超时）
     */
    protected boolean assertHandshake(WebSocket conn) {
        ChannelInternal channel = getChannel(conn);

        if (channel == null || channel.getHandshake() == null) {
            conn.close();

            if (log.isWarnEnabled()) {
                log.warn("Server channel no handshake onPingPong");
            }
            return false;
        } else {
            return true;
        }
    }


    private static class InnerChannelSupporter implements ChannelSupporter<WebSocket> {
        private ToSocketdWebSocketListener l;

        InnerChannelSupporter(ToSocketdWebSocketListener l) {
            this.l = l;
        }

        @Override
        public Processor getProcessor() {
            return l.processor;
        }

        @Override
        public ChannelAssistant<WebSocket> getAssistant() {
            return l.assistant;
        }

        @Override
        public Config getConfig() {
            return l.config;
        }
    }

    private static class InnerChannelAssistant implements ChannelAssistant<WebSocket> {
        private final Config config;

        public InnerChannelAssistant(Config config) {
            this.config = config;
        }

        @Override
        public void write(WebSocket target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
            try {
                ByteBufferCodecWriter writer = config.getCodec().write(frame, len -> new ByteBufferCodecWriter(ByteBuffer.allocate(len)));
                target.send(writer.getBuffer());

                completionHandler.completed(true, null);
            } catch (Throwable e) {
                completionHandler.completed(false, e);
            }
        }

        public Frame read(ByteBuffer buffer) throws IOException {
            return config.getCodec().read(new ByteBufferCodecReader(buffer));
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
}
