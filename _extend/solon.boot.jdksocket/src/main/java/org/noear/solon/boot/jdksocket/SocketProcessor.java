package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;
import org.noear.solon.core.XMethod;
import org.noear.solon.extend.xsocket.XSession;
import org.noear.solon.extend.xsocket.XSocketContextHandler;
import org.noear.solon.extend.xsocket.XSocketListener;
import org.noear.solon.extend.xsocket.XSocketMessage;


public class SocketProcessor {
    private XSocketContextHandler handler;
    private XSocketListener listener;

    public SocketProcessor() {
        handler = new XSocketContextHandler(XMethod.SOCKET);
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

            if (message.getHandled() == false) {
                handler.handle(session, message, false);
            }
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
