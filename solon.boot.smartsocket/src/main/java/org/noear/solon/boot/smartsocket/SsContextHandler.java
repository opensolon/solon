package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;

public class SsContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public SsContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().argx().getInt("debug") == 1;
    }

    public void handle(SsRequest request, SsResponse response) {
        try {
            SsContext context = new SsContext(request, response);

            context.contentType("text/plain;charset=UTF-8");

            xapp.handle(context);

        } catch (Throwable ex) {
            ex.printStackTrace();
            ex.printStackTrace(response.getWriter());
            response.setStatus(500);
        }
    }
}
