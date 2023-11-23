package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.boot.prop.impl.WebSocketServerProps;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.handler.WebSocketDefaultHandler;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.util.AttachKey;
import org.smartboot.socket.util.Attachment;

import java.nio.ByteBuffer;

public class SmWebSocketHandleImpl extends WebSocketDefaultHandler {
    static final Logger log = LoggerFactory.getLogger(SmWebSocketHandleImpl.class);
    static final WebSocketServerProps wsProps = WebSocketServerProps.getInstance();
    static final AttachKey<WebSocketImpl> SESSION_KEY = AttachKey.valueOf("SESSION");

    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        WebSocketImpl webSocket = new WebSocketImpl(request);
        if (request.getAttachment() == null) {
            request.setAttachment(new Attachment());
        }
        request.getAttachment().put(SESSION_KEY, webSocket);

        webSocketRouter.getListener().onOpen(webSocket);

        //设置闲置超时
        if (wsProps.getIdleTimeout() > 0) {
            webSocket.setIdleTimeout(wsProps.getIdleTimeout());
        }
    }

    @Override
    public void onClose(Request request) {
        WebSocketRequest request2 = request.newWebsocketRequest();
        onCloseDo(request2);
    }

    @Override
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        onCloseDo(request);
    }

    private void onCloseDo(WebSocketRequest request) {
        WebSocketImpl webSocket = request.getAttachment().get(SESSION_KEY);
        if (webSocket.isClosed()) {
            return;
        } else {
            webSocket.close();
        }

        webSocketRouter.getListener().onClose(webSocket);
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            WebSocketImpl webSocket = request.getAttachment().get(SESSION_KEY);
            webSocket.onReceive();

            webSocketRouter.getListener().onMessage(webSocket, data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            WebSocketImpl webSocket = request.getAttachment().get(SESSION_KEY);
            webSocket.onReceive();

            webSocketRouter.getListener().onMessage(webSocket, ByteBuffer.wrap(data));
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocketRequest request, Throwable error) {
        try {
            WebSocket webSocket = request.getAttachment().get(SESSION_KEY);
            webSocketRouter.getListener().onError(webSocket, error);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}