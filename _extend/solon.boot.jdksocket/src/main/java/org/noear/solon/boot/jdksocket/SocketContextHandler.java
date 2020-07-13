package org.noear.solon.boot.jdksocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XMonitor;
import org.noear.solonclient.channel.SocketMessage;

import java.io.PrintWriter;

public class SocketContextHandler {
    private XApp xapp;

    public SocketContextHandler(XApp xapp) {
        this.xapp = xapp;
    }

    public void handler(SocketSession session, SocketMessage message) {
        if (message == null) {
            return;
        }

        SocketContext context = new SocketContext(session, message);

        try {
            xapp.handle(context);
        } catch (Throwable ex) {
            XMonitor.sendError(context,ex);
            ex.printStackTrace(new PrintWriter(context.outputStream()));
        }

        try {
            context.commit();
        } catch (Throwable ex) {
            XMonitor.sendError(context,ex);
        }
    }
}
