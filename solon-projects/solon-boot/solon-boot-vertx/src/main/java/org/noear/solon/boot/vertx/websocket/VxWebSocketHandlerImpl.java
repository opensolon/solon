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
package org.noear.solon.boot.vertx.websocket;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.noear.solon.Utils;
import org.noear.solon.boot.web.DecodeUtils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocketBase;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

/**
 * @author noear
 * @since 3.0
 */
public class VxWebSocketHandlerImpl implements Handler<ServerWebSocket> {
    static final Logger log = LoggerFactory.getLogger(VxWebSocketHandlerImpl.class);
    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    public void subProtocolCapable(HttpServerRequest req) {
        //添加子协议支持
        String path = DecodeUtils.rinseUri(req.path());
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            Collection<String> requestProtocols = req.headers().getAll(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL);
            String protocols = subProtocolCapable.getSubProtocols(requestProtocols);

            if (Utils.isNotEmpty(protocols)) {
                String[] protocolArr = protocols.split(",");
                for (String sp : protocolArr) {
                    for (String rp : requestProtocols) {
                        //验证成功 //外面用的是 * 号，所以这里要挡住
                        if (sp.equalsIgnoreCase(rp)) {
                            return;
                        }
                    }
                }
            }
        }

        //移掉后，不会触发内部了的子协议验证（客户端，就收不到子协议申明了）
        req.headers().remove(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL);
    }

    @Override
    public void handle(ServerWebSocket sw) {
        WebSocketBase webSocket = new VxWebSocketImpl(sw);

        sw.frameHandler(frame -> {
            switch (frame.type()) {
                case PING:
                    onPing(webSocket);
                    break;
                case TEXT:
                    onTextMessage(webSocket, frame.textData());
                    break;
                case BINARY:
                    onBinaryMessage(webSocket, frame.binaryData().getBytes());
                    break;
            }
        });

        sw.pongHandler(buf -> {
            onPong(webSocket);
        });

        sw.closeHandler(v -> {
            onClose(webSocket);
        });

        sw.exceptionHandler(error -> {
            onError(webSocket, error);
        });

        onOpen(sw, webSocket);

        sw.accept();
    }

    private void onOpen(ServerWebSocket request, WebSocketBase webSocket) {
        //转移头信息
        for (Map.Entry<String, String> kv : request.headers()) {
            webSocket.paramMap().add(kv.getKey(), kv.getValue());
        }


        webSocketRouter.getListener().onOpen(webSocket);
    }

    private void onPing(WebSocketBase webSocket) {
        webSocketRouter.getListener().onPing(webSocket);
    }

    private void onPong(WebSocketBase webSocket) {
        webSocketRouter.getListener().onPong(webSocket);
    }

    private void onTextMessage(WebSocketBase webSocket, String data) {
        try {
            webSocketRouter.getListener().onMessage(webSocket, data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }


    private void onBinaryMessage(WebSocketBase webSocket, byte[] data) {
        try {
            webSocketRouter.getListener().onMessage(webSocket, ByteBuffer.wrap(data));
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void onClose(WebSocketBase webSocket) {
        if (webSocket.isClosed()) {
            return;
        } else {
            RunUtil.runAndTry(webSocket::close);
        }

        webSocketRouter.getListener().onClose(webSocket);
    }

    private void onError(WebSocketBase webSocket, Throwable error) {
        try {
            webSocketRouter.getListener().onError(webSocket, error);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}