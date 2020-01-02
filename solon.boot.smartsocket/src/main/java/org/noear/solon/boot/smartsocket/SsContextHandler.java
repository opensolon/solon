package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;

import java.io.PrintWriter;

public class SsContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public SsContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().isDebugMode();
    }

    public void handle(SsContext context) {
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
