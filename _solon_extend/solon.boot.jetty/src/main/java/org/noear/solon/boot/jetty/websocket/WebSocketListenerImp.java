package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;

import java.nio.ByteBuffer;

public class WebSocketListenerImp extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(org.eclipse.jetty.websocket.api.Session sess) {
        super.onWebSocketConnect(sess);

        Session session = _SocketServerSession.get(getSession());
        sess.getUpgradeRequest().getHeaders().forEach((k, v) -> {
            if (v.size() > 0) {
                session.headerSet(k, v.get(0));
            }
        });

        Solon.app().listener().onOpen(session);
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(payload, offset, len);
            Session session = _SocketServerSession.get(getSession());

            Message message = null;

            if (Solon.app().enableWebSocketD()) {
                message = ProtocolManager.decode(buf);
            } else {
                message = Message.wrap(getSession().getUpgradeRequest().getOrigin(), null,
                        buf.array());
            }

            Solon.app().listener().onMessage(session, message);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onWebSocketText(String text) {
        try {
            Session session = _SocketServerSession.get(getSession());
            Message message = Message.wrap(getSession().getUpgradeRequest().getRequestURI().toString(), null, text);

            Solon.app().listener().onMessage(session, message.isString(true));

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        Solon.app().listener().onClose(_SocketServerSession.get(getSession()));

        _SocketServerSession.remove(getSession());
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        Solon.app().listener().onError(_SocketServerSession.get(getSession()), cause);
    }
}
