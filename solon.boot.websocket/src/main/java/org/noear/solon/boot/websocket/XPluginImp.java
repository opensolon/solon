package org.noear.solon.boot.websocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

import java.io.Closeable;
import java.io.IOException;

public class XPluginImp implements XPlugin, Closeable {
    private   WsServer _server = null;

    public static String solon_boot_ver(){
        return "Java-WebSocket 1.4.0/1.0.3.6";
    }

    @Override
    public void start(XApp app) {
        int _port = 10000 + app.port();
        long time_start = System.currentTimeMillis();

        WsContextHandler contextHandler = new WsContextHandler(app);

        System.out.println("oejs.Server:main: WebSocket");

        try {
            _server  =new WsServer(_port, contextHandler);

            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[WebSocket]}{0.0.0.0:" + _port + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if(_server != null){
            try {
                _server.stop();
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }
    }
}
