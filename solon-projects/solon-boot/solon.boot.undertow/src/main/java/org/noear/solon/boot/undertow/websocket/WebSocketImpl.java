/*
 * Copyright 2017-2024 noear.org and authors
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

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketBase;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketBase {
    private WebSocketChannel real;

    public WebSocketImpl(WebSocketChannel real) {
        this.real = real;
        this.init(URI.create(real.getUrl()));
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
    public void send(String text) {
        WebSockets.sendText(text, real, CallbackImpl.instance);
    }

    @Override
    public void send(ByteBuffer binary) {
        WebSockets.sendBinary(binary, real, CallbackImpl.instance);
    }

    @Override
    public void close() {
        super.close();
        RunUtil.runAndTry(real::close);
    }
}
