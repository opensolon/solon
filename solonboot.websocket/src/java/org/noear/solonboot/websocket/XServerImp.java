package org.noear.solonboot.websocket;

import org.noear.solonboot.XApp;
import org.noear.solonboot.protocol.XServer;

public class XServerImp implements XServer {
    @Override
    public void start(XApp app) {
        int _port = 10000 + app.port();
        long time_start = System.currentTimeMillis();

        WsContextHandler contextHandler = new WsContextHandler(true, app);

        System.out.println("oejs.Server:main: WebSocket");

        try {
            WsServer server  =new WsServer(_port, contextHandler);

            server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[WebSocket]}{0.0.0.0:" + _port + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
