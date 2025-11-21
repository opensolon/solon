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
package org.noear.solon.server.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.net.websocket.WebSocketBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketBase {
    private static final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);

    private WebSocketChannel real;

    public WebSocketImpl(WebSocketChannel real) {
        this.real = real;
        String uri = DecodeUtils.rinseUri(real.getUrl());

        this.init(URI.create(uri));
    }

    @Override
    public boolean isValid() {
        return isClosed() == false && real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.isSecure();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return real.getSourceAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return real.getDestinationAddress();
    }

    @Override
    public long getIdleTimeout() {
        return real.getIdleTimeout();
    }

    @Override
    public void setIdleTimeout(long idleTimeout) {
        real.setIdleTimeout(idleTimeout);
    }


    @Override
    public Future<Void> send(String text) {
        CallbackFuture future = new CallbackFuture();
        WebSockets.sendText(text, real, future);

        return future;
    }

    @Override
    public Future<Void> send(ByteBuffer binary) {
        CallbackFuture future = new CallbackFuture();
        WebSockets.sendBinary(binary, real, future);

        return future;
    }

    @Override
    public void close() {
        super.close();

        if (real.isOpen()) {
            try {
                real.close();
            } catch (Throwable ignore) {
                if (log.isDebugEnabled()) {
                    log.debug("Close failure: {}", ignore.getMessage());
                }
            }
        }
    }

    @Override
    public void close(int code, String reason) {
        super.close(code, reason);

        if (real.isOpen()) {
            try {
                real.setCloseCode(code);
                real.setCloseReason(reason);
                real.close();
            } catch (Throwable ignore) {
                if (log.isDebugEnabled()) {
                    log.debug("Close failure: {}", ignore.getMessage());
                }
            }
        }
    }
}
