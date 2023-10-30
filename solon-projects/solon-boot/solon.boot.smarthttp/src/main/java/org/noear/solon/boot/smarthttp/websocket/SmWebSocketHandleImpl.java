package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.Solon;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.handler.WebSocketDefaultHandler;
import org.smartboot.http.server.impl.Request;

import java.nio.ByteBuffer;

public class SmWebSocketHandleImpl extends WebSocketDefaultHandler {
    static final Logger log = LoggerFactory.getLogger(SmWebSocketHandleImpl.class);

    @Override
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        Solon.app().listener().onOpen(_SocketServerSession.get(request));
    }

    @Override
    public void onClose(Request request) {
        WebSocketRequest request2 =  request.newWebsocketRequest();
        onCloseDo(request2);
    }

    @Override
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        onCloseDo(request);
    }

    private void onCloseDo(WebSocketRequest request) {
        _SocketServerSession session = _SocketServerSession.get(request);
        if(session.isClosed()){
            return;
        }

        session.onClose();
        Solon.app().listener().onClose(session);
        _SocketServerSession.remove(request);
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            Session session = _SocketServerSession.get(request);
            Message message = Message.wrap(request.getRequestURI(), null, data);

            Solon.app().listener().onMessage(session, message.isString(true));
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            Session session = _SocketServerSession.get(request);
            Message message = null;

            if (Solon.app().enableWebSocketD()) {
                message = ProtocolManager.decode(ByteBuffer.wrap(data));
            } else {
                message = Message.wrap(request.getRequestURI(), null, data);
            }

            Solon.app().listener().onMessage(session, message);

        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocketRequest request, Throwable error) {
        Solon.app().listener().onError(_SocketServerSession.get(request), error);
    }
}
