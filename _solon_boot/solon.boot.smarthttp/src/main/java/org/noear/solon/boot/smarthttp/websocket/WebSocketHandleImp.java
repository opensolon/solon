package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.handler.WebSocketDefaultHandler;

import java.nio.ByteBuffer;

public class WebSocketHandleImp extends WebSocketDefaultHandler {

    @Override
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        Solon.app().listener().onOpen(_SocketServerSession.get(request, response));
    }

    @Override
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        _SocketServerSession session = _SocketServerSession.get(request, response);
        session.onClose();

        Solon.app().listener().onClose(session);

        _SocketServerSession.remove(request);
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            Session session = _SocketServerSession.get(request, response);
            Message message = Message.wrap(request.getRequestURI(), null, data);

            Solon.app().listener().onMessage(session, message.isString(true));
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            Session session = _SocketServerSession.get(request, response);
            Message message = null;

            if (Solon.app().enableWebSocketD()) {
                message = ProtocolManager.decode(ByteBuffer.wrap(data));
            } else {
                message = Message.wrap(request.getRequestURI(), null, data);
            }

            Solon.app().listener().onMessage(session, message);

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onError(WebSocketRequest request, Throwable error) {
        _SocketServerSession session = _SocketServerSession.getOnly(request);

        if (session != null) {
            Solon.app().listener().onError(session, error);
        }
    }
}
