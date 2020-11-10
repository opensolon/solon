package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.core.*;
import org.noear.solon.extend.xsocket.XListenerProxy;
import org.smartboot.http.WebSocketRequest;
import org.smartboot.http.WebSocketResponse;
import org.smartboot.http.server.handle.WebSocketDefaultHandle;

public class WebSocketHandleImp extends WebSocketDefaultHandle {

    @Override
    public void onHandShark(WebSocketRequest request, WebSocketResponse response) {
        XListenerProxy.getGlobal().onOpen(_SocketSession.get(request, response));
    }

    @Override
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        _SocketSession session = _SocketSession.get(request, response);
        session.onClose();

        XListenerProxy.getGlobal().onClose(session);

        _SocketSession.remove(request);
    }

    @Override
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        try {
            XSession session = _SocketSession.get(request, response);
            XMessage message = XMessage.wrap(request.getRequestURI(),
                    data.getBytes("UTF-8"));

            XListenerProxy.getGlobal().onMessage(session, message, true);
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        try {
            XSession session = _SocketSession.get(request, response);
            XMessage message = XMessage.wrap(request.getRequestURI(), data);

            XListenerProxy.getGlobal().onMessage(session, message, false);

        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void onError(Throwable error) {
//        if (listener != null) {
//            listener.onError(_SocketSession.get(request,response), error);
//        }
    }
}
