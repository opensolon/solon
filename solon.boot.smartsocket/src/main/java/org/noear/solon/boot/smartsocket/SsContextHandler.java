package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.transport.AioSession;

import java.io.PrintWriter;

public class SsContextHandler {
    protected XApp xapp;
    protected boolean debug;
    //private ExecutorService pool = Executors.newCachedThreadPool();

    public SsContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().isDebugMode();
    }

    public void handle(AioSession<SocketMessage> session, SocketMessage request) {
        if(request == null){
            return;
        }

        SsContext context = new SsContext(session, request);

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
