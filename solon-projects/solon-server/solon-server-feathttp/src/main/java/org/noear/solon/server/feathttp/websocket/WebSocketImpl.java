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
package org.noear.solon.server.feathttp.websocket;

import org.noear.solon.net.websocket.WebSocketTimeoutBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.smartboot.feat.core.server.WebSocketResponse;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * @author noear
 * @since 2.0
 */
public class WebSocketImpl extends WebSocketTimeoutBase {
    private static final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);
    private final WebSocketResponse real;
    private final InetSocketAddress remoteAddr;
    private final InetSocketAddress localAddr;
    private final boolean secure;

    public WebSocketImpl(String uri, InetSocketAddress remoteAddress, InetSocketAddress localAddress, boolean secure, WebSocketResponse response) {
        this.real = response;
        this.remoteAddr = remoteAddress;
        this.localAddr = localAddress;
        this.secure = secure;

        this.init(URI.create(uri));
    }

    @Override
    public boolean isValid() {
        return isClosed() == false;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return remoteAddr;
    }

    @Override
    public InetSocketAddress localAddress() {
        return localAddr;
    }

    @Override
    public Future<Void> send(String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            real.sendTextMessage(text);
            real.flush();
            onSend();

            future.complete(null);
        } catch (Throwable ex) {
            future.completeExceptionally(ex);
        }

        return future;
    }

    @Override
    public Future<Void> send(ByteBuffer binary) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            real.sendBinaryMessage(binary.array());
            real.flush();

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
            real.close();
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
            real.close(code, reason);
        } catch (Throwable ignore) {
            if (log.isDebugEnabled()) {
                log.debug("Close failure: {}", ignore.getMessage());
            }
        }
    }
}
