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
package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.Utils;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.http.common.codec.websocket.CloseReason;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.handler.WebSocketDefaultHandler;
import org.smartboot.http.server.impl.Request;
import org.smartboot.http.server.impl.WebSocketRequestImpl;
import org.smartboot.http.server.impl.WebSocketResponseImpl;
import org.smartboot.socket.util.AttachKey;
import org.smartboot.socket.util.Attachment;

import java.net.URI;
import java.nio.ByteBuffer;

public class SmWebSocketHandleImpl extends WebSocketDefaultHandler {
    static final Logger log = LoggerFactory.getLogger(SmWebSocketHandleImpl.class);
    static final AttachKey<WebSocketImpl> SESSION_KEY = AttachKey.valueOf("SESSION");

    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();


    @Override
    public void willHeaderComplete(WebSocketRequestImpl request, WebSocketResponseImpl response) {
        super.willHeaderComplete(request, response);

        //添加子协议支持
        String uri = DecodeUtils.rinseUri(request.getRequestURL());
        String path = URI.create(uri).getPath();
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            String protocols = subProtocolCapable.getSubProtocols(request.getHeaders(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL));

            if (Utils.isNotEmpty(protocols)) {
                response.setHeader(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL, protocols);
            }
        }
    }

    @Override
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        WebSocketRequestImpl request1 = (WebSocketRequestImpl) request;

        WebSocketImpl webSocket = new WebSocketImpl(request1);

        //转移头信息
        request1.getHeaderNames().forEach(name -> {
            webSocket.param(name, request1.getHeader(name));
        });

        //添加附件
        if (request1.getAttachment() == null) {
            request1.setAttachment(new Attachment());
        }
        request1.getAttachment().put(SESSION_KEY, webSocket);

        webSocketRouter.getListener().onOpen(webSocket);
    }

    @Override
    public void onClose(Request request) {
        WebSocketRequest request2 = request.newWebsocketRequest();
        onCloseDo(request2);
    }

    @Override
    public void onClose(WebSocketRequest request, WebSocketResponse response, CloseReason closeReason)  {
        onCloseDo(request);
    }

    private void onCloseDo(WebSocketRequest request) {
        WebSocketRequestImpl request1 = (WebSocketRequestImpl) request;

        try {
            WebSocketImpl webSocket = request1.getAttachment().get(SESSION_KEY);
            if (webSocket.isClosed()) {
                return;
            } else {
                RunUtil.runAndTry(webSocket::close);
            }

            webSocketRouter.getListener().onClose(webSocket);
        } finally {
            request1.getAttachment().remove(SESSION_KEY);
        }
    }

    @Override
    public void handlePing(WebSocketRequest request, WebSocketResponse response) {
        super.handlePing(request, response);

        WebSocketRequestImpl request1 = (WebSocketRequestImpl) request;
        WebSocketImpl webSocket = request1.getAttachment().get(SESSION_KEY);
        if (webSocket != null) {
            webSocket.onReceive();
        }

        webSocketRouter.getListener().onPing(webSocket);
    }

    @Override
    public void handlePong(WebSocketRequest request, WebSocketResponse response) {
        super.handlePong(request, response);

        WebSocketRequestImpl request1 = (WebSocketRequestImpl) request;
        WebSocketImpl webSocket = request1.getAttachment().get(SESSION_KEY);
        if (webSocket != null) {
            webSocket.onReceive();
        }

        webSocketRouter.getListener().onPong(webSocket);
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            WebSocketRequestImpl request1 = (WebSocketRequestImpl) request;
            WebSocketImpl webSocket = request1.getAttachment().get(SESSION_KEY);
            webSocket.onReceive();

            webSocketRouter.getListener().onMessage(webSocket, data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            WebSocketRequestImpl request1 = (WebSocketRequestImpl) request;
            WebSocketImpl webSocket = request1.getAttachment().get(SESSION_KEY);
            webSocket.onReceive();

            webSocketRouter.getListener().onMessage(webSocket, ByteBuffer.wrap(data));
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }


    @Override
    public void onError(WebSocketRequest request, Throwable error) {
        try {
            WebSocketRequestImpl request1 = (WebSocketRequestImpl) request;
            WebSocket webSocket = request1.getAttachment().get(SESSION_KEY);
            webSocketRouter.getListener().onError(webSocket, error);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}