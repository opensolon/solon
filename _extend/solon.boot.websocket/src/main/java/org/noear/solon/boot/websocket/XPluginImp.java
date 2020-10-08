package org.noear.solon.boot.websocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    private WsServer _server = null;

    public static String solon_boot_ver() {
        return "org.java_websocket 1.5.0/1.0.43";
    }

    @Override
    public void start(XApp app) {
        if(app.enableWebSocket() == false){
            return;
        }


        int _port = 10000 + app.port();
        long time_start = System.currentTimeMillis();


        System.out.println("solon.Server:main: org.java_websocket 1.5.0(websocket)");

        try {
            _server = new WsServer(_port);

            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: websocket: Started ServerConnector@{HTTP/1.1,[WebSocket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: websocket: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            System.out.println("solon.Server:main: websocket: Has Stopped " + solon_boot_ver());
        }
    }
}
