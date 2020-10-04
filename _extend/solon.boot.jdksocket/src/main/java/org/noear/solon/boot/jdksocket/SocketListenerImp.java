package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.*;
import org.noear.solon.extend.xsocket.XListenerProxy;
import org.noear.solon.extend.xsocket.XSocketContextHandler;

import java.net.Socket;

public class SocketListenerImp {
    private XSocketContextHandler handler;
    private XListener listener;

    public SocketListenerImp() {
        handler = new XSocketContextHandler(XMethod.SOCKET);
        listener = XListenerProxy.getGlobal();
    }

    public void onOpen(Socket socket) {
        if (listener != null) {
            listener.onOpen(_SocketSession.get(socket));
        }
    }


    public void onMessage(Socket socket, XMessage message) {
        try {
            XSession session = _SocketSession.get(socket);

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

    public void onClosed(Socket socket) {
        if (listener != null) {
            listener.onClose(_SocketSession.get(socket));
        }

        _SocketSession.remove(socket);
    }

    public void onError(Socket socket, Throwable error) {
        if (listener != null) {
            listener.onError(_SocketSession.get(socket), error);
        }
    }
}