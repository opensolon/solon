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
package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Pooled;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {
    private static final Logger log = LoggerFactory.getLogger(UtWsChannelListener.class);
    private final String SESSION_KEY = "session";

    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void handleEvent(WebSocketChannel channel) {
        //
        //window 下，客户端关闭时仍可能会进入这里
        //
        StreamSourceFrameChannel result = null;
        boolean receiveThrowable = false;
        try {
            result = channel.receive();
        } catch (Throwable e) {
            receiveThrowable = true;
            if (e instanceof SocketException == false) {
                this.onError(channel, e);
            }
        }

        try {
            if (result == null) {
                if (channel.isOpen() == false || receiveThrowable) {
                    //如果已关闭，由触发关闭事件 //如果不触发，客户端关了都没感觉
                    this.onClose(channel, result);
                }
                return;
            }

            if (result.getType() == WebSocketFrameType.BINARY) {
                this.onBinary(channel, result);
            } else if (result.getType() == WebSocketFrameType.TEXT) {
                this.onText(channel, result);
            } else if (result.getType() == WebSocketFrameType.PONG) {
                this.onPong(channel, result);
            } else if (result.getType() == WebSocketFrameType.PING) {
                this.onPing(channel, result);
            } else if (result.getType() == WebSocketFrameType.CLOSE) {
                this.onClose(channel, result);
            }
        } catch (IOException e) {
            this.onError(channel, e);
        }

    }


    public void onOpen(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        WebSocketImpl webSocket = new WebSocketImpl(channel);
        exchange.getRequestHeaders().forEach((k, v) -> {
            if (v.size() > 0) {
                webSocket.param(k, v.get(0));
            }
        });

        channel.setAttribute(SESSION_KEY, webSocket);
        webSocketRouter.getListener().onOpen(webSocket);
    }


    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
        try {
            Pooled<ByteBuffer[]> pulledData = msg.getData();

            try {
                ByteBuffer[] resource = pulledData.getResource();
                ByteBuffer byteBuffer = WebSockets.mergeBuffers(resource);

                WebSocketImpl webSocket = (WebSocketImpl) channel.getAttribute(SESSION_KEY);
                webSocketRouter.getListener().onMessage(webSocket, byteBuffer);
            } finally {
                pulledData.discard();
            }

        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
        try {
            WebSocketImpl webSocket = (WebSocketImpl) channel.getAttribute(SESSION_KEY);
            webSocketRouter.getListener().onMessage(webSocket, msg.getData());
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        WebSocketImpl webSocket = (WebSocketImpl) channel.getAttribute(SESSION_KEY);
        if (webSocket.isClosed()) {
            return;
        } else {
            RunUtil.runAndTry(webSocket::close);
        }
        webSocketRouter.getListener().onClose(webSocket);
    }

    @Override
    protected void onPing(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
        super.onPing(webSocketChannel, channel);

        WebSocketImpl webSocket = (WebSocketImpl) webSocketChannel.getAttribute(SESSION_KEY);
        webSocketRouter.getListener().onPing(webSocket);
    }

    @Override
    protected void onPong(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
        super.onPong(webSocketChannel, messageChannel);

        WebSocketImpl webSocket = (WebSocketImpl) webSocketChannel.getAttribute(SESSION_KEY);
        webSocketRouter.getListener().onPong(webSocket);
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        try {
            WebSocketImpl webSocket = (WebSocketImpl) channel.getAttribute(SESSION_KEY);
            webSocketRouter.getListener().onError(webSocket, error);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}