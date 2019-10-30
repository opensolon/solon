package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.XApp;

import java.io.PrintWriter;

public class WsContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public WsContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().argx().getInt("debug") == 1;
    }

    public void handle(WebSocket socket, byte[] message, boolean messageIsString) {

        WsContext context = new WsContext(socket, message, messageIsString);

        try {
            xapp.handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();
            ex.printStackTrace(new PrintWriter(context.outputStream()));
        }

        try {
            context.commit();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
