package org.noear.solon.boot.socketd.websocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.socketd.SessionManager;

public class XPluginImp implements Plugin {
    private WsServer _server = null;

    public static String solon_boot_ver() {
        return "org.java_websocket 1.5.0/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        if (app.enableWebSocket() == false) {
            return;
        }

        SessionManager.register(new _SessionManagerImpl());

        int _port = app.cfg().getInt("server.websocket.port", 0);
        if (_port < 1) {
            _port = 5000 + app.port();
        }

        long time_start = System.currentTimeMillis();


        System.out.println("solon.Server:main: org.java_websocket 1.5.0(websocketd)");

        try {
            _server = new WsServer(_port);

            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: websocketd: Started ServerConnector@{HTTP/1.1,[WebSocket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: websocketd: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            System.out.println("solon.Server:main: websocketd: Has Stopped " + solon_boot_ver());
        }
    }
}
