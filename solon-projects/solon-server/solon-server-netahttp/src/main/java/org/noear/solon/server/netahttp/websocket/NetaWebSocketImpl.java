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
package org.noear.solon.server.netahttp.websocket;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.ProtoContext;
import net.hasor.neta.codec.http.websocket.*;
import org.noear.solon.net.websocket.WebSocketTimeoutBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 基于 neta 的 WebSocket 会话实现
 *
 * @author noear
 * @since 3.10
 */
public class NetaWebSocketImpl extends WebSocketTimeoutBase {
    private static final Logger log = LoggerFactory.getLogger(NetaWebSocketImpl.class);

    private final ProtoContext protoContext;

    public NetaWebSocketImpl(ProtoContext protoContext, String uri) {
        this.protoContext = protoContext;
        this.init(URI.create(uri));
    }

    @Override
    public boolean isValid() {
        return !isClosed();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        try {
            return (InetSocketAddress) protoContext.getChannel().getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public InetSocketAddress localAddress() {
        try {
            return (InetSocketAddress) protoContext.getChannel().getLocalAddr();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Future<Void> send(String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf = ByteBuf.wrap(bytes);
            TextWebSocketMessage msg = TextWebSocketMessage.request(buf);
            protoContext.sendData(msg);
            onSend();
            future.complete(null);
        } catch (Throwable ex) {
            future.completeExceptionally(ex);
        }
        return future;
    }

    @Override
    public Future<Void> send(ByteBuffer data) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            byte[] bytes;
            if (data.hasArray()) {
                bytes = new byte[data.remaining()];
                System.arraycopy(data.array(), data.arrayOffset() + data.position(), bytes, 0, data.remaining());
            } else {
                bytes = new byte[data.remaining()];
                data.get(bytes);
            }
            ByteBuf buf = ByteBuf.wrap(bytes);
            BinaryWebSocketMessage msg = BinaryWebSocketMessage.request(buf);
            protoContext.sendData(msg);
            onSend();
            future.complete(null);
        } catch (Throwable ex) {
            future.completeExceptionally(ex);
        }
        return future;
    }

    @Override
    public void close() {
        super.close();
        try {
            WebSocketCloseEvent closeEvent = new WebSocketCloseEvent(1000, "normal closure");
            protoContext.sendData(closeEvent);
        } catch (Throwable ignore) {
            if (log.isDebugEnabled()) {
                log.debug("Close failure: {}", ignore.getMessage());
            }
        }
    }

    @Override
    public void close(int code, String reason) {
        super.close(code, reason);
        try {
            WebSocketCloseEvent closeEvent = new WebSocketCloseEvent(code, reason);
            protoContext.sendData(closeEvent);
        } catch (Throwable ignore) {
            if (log.isDebugEnabled()) {
                log.debug("Close failure: {}", ignore.getMessage());
            }
        }
    }
}
