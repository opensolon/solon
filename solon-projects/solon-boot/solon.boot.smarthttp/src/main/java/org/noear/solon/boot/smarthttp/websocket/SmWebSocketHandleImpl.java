package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketBus;
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
    static final AttachKey<_WebSokcetImpl> SESSION_KEY = AttachKey.valueOf("SESSION");

    @Override
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        _WebSokcetImpl webSokcet = new _WebSokcetImpl(request);
        if (request.getAttachment() == null) {
            request.setAttachment(new Attachment());
        }
        request.getAttachment().put(SESSION_KEY, webSokcet);

        WebSocketBus.getListener().onOpen(webSokcet);
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
        _WebSokcetImpl webSokcet = request.getAttachment().get(SESSION_KEY);
        if (webSokcet.isClosed()) {
            return;
        } else {
            webSokcet.setClosed(true);
        }

        WebSocketBus.getListener().onClose(webSokcet);
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            WebSocket webSokcet = request.getAttachment().get(SESSION_KEY);
            WebSocketBus.getListener().onMessage(webSokcet, data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            WebSocket webSokcet = request.getAttachment().get(SESSION_KEY);
            WebSocketBus.getListener().onMessage(webSokcet, ByteBuffer.wrap(data));
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocketRequest request, Throwable error) {
        try {
            WebSocket webSokcet = request.getAttachment().get(SESSION_KEY);
            WebSocketBus.getListener().onError(webSokcet, error);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}