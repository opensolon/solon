package org.noear.solon.boot.websocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.*;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;

public class XPluginImp implements Plugin {
    private static Signal _signal;
    public static Signal signal(){
        return _signal;
    }

    private WsServer _server = null;

    public static String solon_boot_ver() {
        return "org.java_websocket 1.5.0/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if (app.enableWebSocket() == false) {
            return;
        }

        Aop.beanOnloaded(() -> {
            start0(app);
        });
    }

    private void start0(SolonApp app) {
        String _name = app.cfg().get("server.websocket.name");
        int _port = app.cfg().getInt("server.websocket.port", 0);
        if (_port < 1) {
            _port = 10000 + app.port();
        }

        long time_start = System.currentTimeMillis();


        PrintUtil.info("Server:main: org.java_websocket 1.5.0(websocket)");

        try {
            _server = new WsServer(_port);

            _server.start();

            _signal = new SignalSim(_name, _port, "ws", SignalType.WEBSOCKET);

            app.signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: websocket: Started ServerConnector@{HTTP/1.1,[WebSocket]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: websocket: Started @" + (time_end - time_start) + "ms");
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            PrintUtil.info("Server:main: websocket: Has Stopped " + solon_boot_ver());
        }
    }
}
