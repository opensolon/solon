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

import org.noear.solon.Utils;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.smartboot.feat.core.common.codec.websocket.CloseReason;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.WebSocketRequest;
import tech.smartboot.feat.core.server.WebSocketResponse;
import tech.smartboot.feat.core.server.upgrade.websocket.WebSocketUpgrade;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author airhead
 */
public class FeatHttpWebSocketUpgrade extends WebSocketUpgrade {
    static final Logger log = LoggerFactory.getLogger(FeatHttpWebSocketUpgrade.class);

    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();
    private WebSocketImpl webSocket;

    private String requestUrl;
    private String queryString;
    private InetSocketAddress remoteAddress;
    private InetSocketAddress localAddress;
    private boolean secure;
    private Collection<String> headerNames;
    private Function<String, Collection<String>> headerGetter;

    /**
     * 在 HTTP handler 中调用，设置子协议头
     */
    public void setupSubProtocol(HttpRequest httpRequest) {
        String uri = DecodeUtils.rinseUri(httpRequest.getRequestURL());
        String path = URI.create(uri).getPath();
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            String protocols = subProtocolCapable.getSubProtocols(httpRequest.getHeaders(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL));
            if (Utils.isNotEmpty(protocols)) {
                httpRequest.getResponse().setHeader(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL, protocols);
            }
        }
    }

    /**
     * 由 SmHttpContextHandler 在调用 request.upgrade() 之前调用，保存 HTTP 请求信息
     */
    public void captureHttpRequest(HttpRequest httpRequest) {
        this.requestUrl = httpRequest.getRequestURL();
        this.queryString = httpRequest.getQueryString();
        this.remoteAddress = httpRequest.getRemoteAddress();
        this.localAddress = httpRequest.getLocalAddress();
        this.secure = httpRequest.isSecure();
        this.headerNames = httpRequest.getHeaderNames();
        final HttpRequest req = httpRequest;
        this.headerGetter = req::getHeaders;
    }

    @Override
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        String uri = buildUri();

        webSocket = new WebSocketImpl(uri, remoteAddress, localAddress, secure, response);

        //转移头信息
        if (headerNames != null) {
            headerNames.forEach(name -> {
                headerGetter.apply(name).forEach(val -> webSocket.param(name, val));
            });
        }

        webSocketRouter.getListener().onOpen(webSocket);
    }

    private String buildUri() {
        if (Utils.isEmpty(queryString)) {
            return DecodeUtils.rinseUri(requestUrl);
        } else {
            if (requestUrl.contains("?")) {
                return DecodeUtils.rinseUri(requestUrl);
            } else {
                return DecodeUtils.rinseUri(requestUrl) + "?" + queryString;
            }
        }
    }

    @Override
    public void onClose(WebSocketRequest request, WebSocketResponse response, CloseReason closeReason) {
        onCloseDo();
    }

    @Override
    public void destroy() {
        if (webSocket != null && !webSocket.isClosed()) {
            onCloseDo();
        }
    }

    private void onCloseDo() {
        if (webSocket == null) {
            return;
        }

        try {
            if (webSocket.isClosed()) {
                return;
            } else {
                RunUtil.runAndTry(webSocket::close);
            }

            webSocketRouter.getListener().onClose(webSocket);
        } finally {
            webSocket = null;
        }
    }

    @Override
    public void handlePing(WebSocketRequest request, WebSocketResponse response) {
        super.handlePing(request, response);

        if (webSocket != null) {
            webSocket.onReceive();
            webSocketRouter.getListener().onPing(webSocket);
        }
    }

    @Override
    public void handlePong(WebSocketRequest request, WebSocketResponse response) {
        super.handlePong(request, response);

        if (webSocket != null) {
            webSocket.onReceive();
            webSocketRouter.getListener().onPong(webSocket);
        }
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            if (webSocket != null) {
                webSocket.onReceive();
                webSocketRouter.getListener().onMessage(webSocket, data);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            if (webSocket != null) {
                webSocket.onReceive();
                webSocketRouter.getListener().onMessage(webSocket, ByteBuffer.wrap(data));
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }


    @Override
    public void onError(WebSocketRequest request, Throwable error) {
        try {
            if (webSocket != null) {
                webSocketRouter.getListener().onError(webSocket, error);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}
