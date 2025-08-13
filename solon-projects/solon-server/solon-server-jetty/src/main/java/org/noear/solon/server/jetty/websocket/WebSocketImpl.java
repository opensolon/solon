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
package org.noear.solon.server.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketBase {
    private final Session real;

    public WebSocketImpl(Session real) {
        this.real = real;
        String uri = DecodeUtils.rinseUri(real.getUpgradeRequest().getRequestURI().toString());

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
    public InetSocketAddress remoteAddress() throws IOException {
        return real.getRemoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() throws IOException {
        return real.getLocalAddress();
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
        return real.getRemote().sendStringByFuture(text);
    }

    @Override
    public Future<Void> send(ByteBuffer binary) {
        return real.getRemote().sendBytesByFuture(binary);
    }

    @Override
    public void close() {
        super.close();
        RunUtil.runAndTry(real::close);
    }
}