package org.noear.solon.boot.websocket;

import org.noear.solon.XApp;

public class WsContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public WsContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().argx().getInt("debug") == 1;
    }

    public void handle(WsRequest request, WsResponse response) {
        try {
            WsContext context = new WsContext(request, response);

            context.contentType("text/plain;charset=UTF-8");

            xapp.handle(context);

        } catch (Throwable ex) {
            ex.printStackTrace();
            ex.printStackTrace(response.getWriter());
            response.setStatus(500);
        }
    }
}
