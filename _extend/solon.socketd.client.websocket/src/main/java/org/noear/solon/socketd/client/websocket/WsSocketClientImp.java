package org.noear.solon.socketd.client.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ListenerManager;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionFlag;

import java.net.URI;
import java.nio.ByteBuffer;

public class WsSocketClientImp extends WebSocketClient {
    private Session session;

    public WsSocketClientImp(URI serverUri, Session session) {
        super(serverUri);
        this.session = session;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        ListenerManager.getPipeline().onOpen(session);
    }

    @Override
    public void onMessage(String test) {
        try {
            ListenerManager.getPipeline().onMessage(session, Message.wrap(test).isString(true));
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {

        try {
            Message message = null;
            if (isWebSocketD()) {
                message = ProtocolManager.decode(bytes);
            } else {
                message = Message.wrap(bytes.array());
            }

            ListenerManager.getPipeline().onMessage(session, message);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        ListenerManager.getPipeline().onClose(session);
    }

    @Override
    public void onError(Exception e) {
        ListenerManager.getPipeline().onError(session, e);
    }

    private boolean isWebSocketD() {
        return Solon.global().enableWebSocketD() || session.flag() == SessionFlag.socketd;
    }
}
