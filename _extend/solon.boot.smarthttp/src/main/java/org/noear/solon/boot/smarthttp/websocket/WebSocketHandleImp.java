package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.MessageListenerProxy;
import org.smartboot.http.WebSocketRequest;
import org.smartboot.http.WebSocketResponse;
import org.smartboot.http.server.handle.WebSocketDefaultHandle;

public class WebSocketHandleImp extends WebSocketDefaultHandle {

    @Override
    public void onHandShark(WebSocketRequest request, WebSocketResponse response) {
        MessageListenerProxy.getGlobal().onOpen(_SocketSession.get(request, response));
    }

    @Override
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        _SocketSession session = _SocketSession.get(request, response);
        session.onClose();

        MessageListenerProxy.getGlobal().onClose(session);

        _SocketSession.remove(request);
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            Session session = _SocketSession.get(request, response);
            Message message = Message.wrap(request.getRequestURI(),
                    data.getBytes("UTF-8"));

            MessageListenerProxy.getGlobal().onMessage(session, message, true);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            Session session = _SocketSession.get(request, response);
            Message message = Message.wrap(request.getRequestURI(), data);

            MessageListenerProxy.getGlobal().onMessage(session, message, false);

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onError(Throwable error) {
//        if (listener != null) {
//            listener.onError(_SocketSession.get(request,response), error);
//        }
    }
}
