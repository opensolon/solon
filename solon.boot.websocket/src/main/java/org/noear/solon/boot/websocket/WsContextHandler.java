package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;

import java.io.PrintWriter;

public class WsContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public WsContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().isDebugMode();
    }

    public void handle(WebSocket socket, byte[] message, boolean messageIsString) {
        SocketMessage request = new SocketMessage(null, socket.getResourceDescriptor(), message);
        WsContext context = new WsContext(socket, request, messageIsString);

        try {
            xapp.handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();
            ex.printStackTrace(new PrintWriter(context.outputStream()));
        }

        try {
            context.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
