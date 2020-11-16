package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.extend.xsocket.MessageListenerProxy;

import java.nio.ByteBuffer;

public class WebSocketListenerImp extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(org.eclipse.jetty.websocket.api.Session sess) {
        super.onWebSocketConnect(sess);
        MessageListenerProxy.getGlobal().onOpen(_SocketSession.get(getSession()));
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(payload, offset, len);
            MessageSession session = _SocketSession.get(getSession());
            Message message = Message.wrap(getSession().getUpgradeRequest().getOrigin(),
                    buf.array());

            MessageListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onWebSocketText(String text) {
        try {
            MessageSession session = _SocketSession.get(getSession());
            Message message = Message.wrap(getSession().getUpgradeRequest().getRequestURI().toString(),
                    text.getBytes("UTF-8"));

            MessageListenerProxy.getGlobal().onMessage(session, message, true);

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        MessageListenerProxy.getGlobal().onClose(_SocketSession.get(getSession()));

        _SocketSession.remove(getSession());
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        MessageListenerProxy.getGlobal().onError(_SocketSession.get(getSession()), cause);
    }
}
