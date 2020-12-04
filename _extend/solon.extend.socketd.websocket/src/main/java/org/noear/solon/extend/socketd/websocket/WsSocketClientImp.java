package org.noear.solon.extend.socketd.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;
import org.noear.solon.extend.socketd.MessageUtils;
import org.noear.solon.extend.socketd.MessageWrapper;

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
        ListenerProxy.getGlobal().onOpen(session);
    }

    @Override
    public void onMessage(String s) {
        try {
            ListenerProxy.getGlobal().onMessage(session, MessageWrapper.wrap(s.getBytes()), true);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {

        try {
            Message message = null;
            if (Solon.global().enableWebSocketD()) {
                message = MessageUtils.decode(bytes);
            } else {
                message = MessageWrapper.wrap(bytes.array());
            }

            ListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        ListenerProxy.getGlobal().onClose(session);
    }

    @Override
    public void onError(Exception e) {
        ListenerProxy.getGlobal().onError(session, e);
    }
}
