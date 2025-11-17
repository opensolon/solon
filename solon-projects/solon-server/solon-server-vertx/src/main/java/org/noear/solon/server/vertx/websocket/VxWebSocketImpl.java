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
package org.noear.solon.server.vertx.websocket;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.SocketAddress;
import org.noear.solon.Utils;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.net.websocket.WebSocketTimeoutBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

/**
 * @author noear
 * @since 3.0
 */
public class VxWebSocketImpl extends WebSocketTimeoutBase {
    private static final Logger log = LoggerFactory.getLogger(VxWebSocketImpl.class);
    private ServerWebSocket real;

    public VxWebSocketImpl(ServerWebSocket real) {
        this.real = real;
        String uri = buildUri(real);

        this.init(URI.create(uri));
    }

    public String buildUri(ServerWebSocket req) {
        if (Utils.isEmpty(req.query())) {
            return DecodeUtils.rinseUri(req.uri());
        } else {
            if (req.uri().contains("?")) {
                return DecodeUtils.rinseUri(req.uri());
            } else {
                return DecodeUtils.rinseUri(req.uri()) + "?" + req.query();
            }
        }
    }

    private static InetSocketAddress toInetSocketAddress(SocketAddress socketAddress) {
        return new InetSocketAddress(socketAddress.hostAddress(), socketAddress.port());
    }

    @Override
    public boolean isValid() {
        return isClosed() == false;
    }

    @Override
    public boolean isSecure() {
        return real.isSsl();
    }

    private InetSocketAddress remoteAddress;

    @Override
    public InetSocketAddress remoteAddress() {
        if (remoteAddress == null) {
            remoteAddress = toInetSocketAddress(real.remoteAddress());
        }

        return remoteAddress;
    }

    private InetSocketAddress localAddress;

    @Override
    public InetSocketAddress localAddress() {
        if (localAddress == null) {
            localAddress = toInetSocketAddress(real.localAddress());
        }

        return localAddress;
    }

    @Override
    public Future<Void> send(String text) {
        CallbackFuture future = new CallbackFuture();
        real.writeFinalTextFrame(text, future);
        onSend();

        return future;
    }

    @Override
    public Future<Void> send(ByteBuffer binary) {
        CallbackFuture future = new CallbackFuture();
        real.writeBinaryMessage(Buffer.buffer(binary.array()), future);
        onSend();

        return future;
    }

    @Override
    public void close() {
        super.close();

        if (real.isClosed() == false) {
            real.close().onFailure(ignore -> {
                log.debug("Close failure: {}", ignore.getMessage());
            });
        }
    }

    @Override
    public void close(int code, String reason) {
        super.close(code, reason);

        if (real.isClosed() == false) {
            real.close((short) code, reason).onFailure(ignore -> {
                log.debug("Close failure: {}", ignore.getMessage());
            });
        }
    }
}