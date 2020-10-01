package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;
import org.noear.solonx.socket.api.XSession;
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

    public void onOpen(XSession session) {
        if (listener != null) {
            listener.onOpen(session);
        }
    }


    public void onMessage(XSession session, XSocketMessage message) {
        try {
            if (listener != null) {
                listener.onMessage(session, message);
            }

            handler.handle(session, message);
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    public void onClosed(XSession session) {
        if (listener != null) {
            listener.onClose(session);
        }
    }
}
