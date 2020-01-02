package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.transport.AioSession;

import java.io.PrintWriter;

public class AioContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public AioContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().isDebugMode();
    }

    public void handle(AioSession<SocketMessage> session, SocketMessage request) {
        if (request == null) {
            return;
        }

        AioContext context = new AioContext(session, request);

        try {
            xapp.handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();
            ex.printStackTrace(new PrintWriter(context.outputStream()));
        }

        try {
            context.commit();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
