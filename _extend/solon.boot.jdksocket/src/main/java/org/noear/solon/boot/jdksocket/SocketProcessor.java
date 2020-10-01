package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;
import org.noear.solonx.socket.api.XSocketListener;
import org.noear.solonx.socket.api.XSocketMessage;

import java.net.Socket;

public class SocketProcessor {
    private StContextHandler handler;
    private XSocketListener listener;

    public SocketProcessor() {
        handler = new StContextHandler();
        Aop.getAsyn(XSocketListener.class, (bw) -> listener = bw.raw());
    }

    public SocketSession onOpen(Socket socket) {
        if (listener != null) {
            listener.onOpen(_SocketSession.get(socket));
        }

        return new SocketSession(socket);
    }


    public void onMessage(SocketSession session, XSocketMessage message) {
        try {
            if (listener != null) {
                listener.onMessage(_SocketSession.get(session.connector), message);
            }

            handler.handle(session, message);
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    public void onClosed(Socket socket) {
        if (listener != null) {
            listener.onClose(_SocketSession.get(socket));
        }
    }
}
