package org.noear.solon.boot.websocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    private   WsServer server = null;
    @Override
    public void start(XApp app) {
        int _port = 10000 + app.port();
        long time_start = System.currentTimeMillis();

        WsContextHandler contextHandler = new WsContextHandler(true, app);

        System.out.println("oejs.Server:main: WebSocket");

        try {
            server  =new WsServer(_port, contextHandler);

            server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[WebSocket]}{0.0.0.0:" + _port + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
