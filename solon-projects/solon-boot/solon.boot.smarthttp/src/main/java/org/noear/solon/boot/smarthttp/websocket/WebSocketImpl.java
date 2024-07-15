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
package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.Utils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketTimeoutBase;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.impl.WebSocketRequestImpl;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class WebSocketImpl extends WebSocketTimeoutBase {
    private final WebSocketRequestImpl request;
    private final WebSocketResponse real;
    public WebSocketImpl(WebSocketRequest request) {
        this.request = ((WebSocketRequestImpl) request);
        this.real = this.request.getResponse();
        this.init(buildUri(request));
    }

    public URI buildUri(WebSocketRequest req) {
        if (Utils.isEmpty(req.getQueryString())) {
            return URI.create(req.getRequestURL());
        } else {
            if (req.getRequestURL().contains("?")) {
                return URI.create(req.getRequestURL());
            } else {
                return URI.create(req.getRequestURL() + "?" + req.getQueryString());
            }
        }
    }

    @Override
    public boolean isValid() {
        return isClosed() == false;
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return request.getRemoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return request.getLocalAddress();
    }

    @Override
    public void send(String text) {
        real.sendTextMessage(text);
        real.flush();

        onSend();
    }

    @Override
    public void send(ByteBuffer binary) {
        real.sendBinaryMessage(binary.array());
        real.flush();

        onSend();
    }

    @Override
    public void close() {
        super.close();
        RunUtil.runAndTry(real::close);
    }
}
