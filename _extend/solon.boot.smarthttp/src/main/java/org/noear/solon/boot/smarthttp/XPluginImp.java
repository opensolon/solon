package org.noear.solon.boot.smarthttp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.smarthttp.http.SmartHttpContextHandler;
import org.noear.solon.boot.smarthttp.http.FormContentFilter;
import org.noear.solon.boot.smarthttp.websocket.WebSocketHandleImp;
import org.noear.solon.boot.smarthttp.websocket._SessionManagerImpl;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;
import org.smartboot.http.server.HttpBootstrap;

public final class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    HttpBootstrap _server = null;

    public static String solon_boot_ver() {
        return "smart http 1.1/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        if (app.enableHttp() == false) {
            return;
        }

        String _name = app.cfg().get("server.http.name");
        int _port = app.cfg().getInt("server.http.port", 0);
        if (_port < 1) {
            _port = app.port();
        }

        long time_start = System.currentTimeMillis();

        SmartHttpContextHandler _handler = new SmartHttpContextHandler();

        _server = new HttpBootstrap();
        _server.configuration()
                .bannerEnabled(false)
                .readBufferSize(1024 * 8) //默认: 8k
                .threadNum(Runtime.getRuntime().availableProcessors() + 2);


        if (ServerProps.request_maxHeaderSize != 0) {
            _server.configuration()
                    .readBufferSize(ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxRequestSize != 0) {
            _server.configuration()
                    .setMaxFormContentSize(ServerProps.request_maxRequestSize);
        }


        //HttpServerConfiguration
        EventBus.push(_server.configuration());

        _server.httpHandler(_handler);

        if (app.enableWebSocket()) {
            _server.webSocketHandler(new WebSocketHandleImp());

            SessionManager.register(new _SessionManagerImpl());
        }

        PrintUtil.info("Server:main: SmartHttpServer 1.1.4(smarthttp)");

        try {

            _server.setPort(_port);

            _server.start();

            _signal = new SignalSim(_name, _port, "http", SignalType.HTTP);

            app.signalAdd(_signal);

            app.before(-9, new FormContentFilter());

            long time_end = System.currentTimeMillis();

            String connectorInfo = "solon.connector:main: smarthttp: Started ServerConnector@{HTTP/1.1,[http/1.1]";
            if (app.enableWebSocket()) {
                PrintUtil.info(connectorInfo + "[WebSocket]}{0.0.0.0:" + _port + "}");
            }

            PrintUtil.info(connectorInfo + "}{http://localhost:" + _port + "}");

            PrintUtil.info("Server:main: smarthttp: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.shutdown();
            _server = null;

            PrintUtil.info("Server:main: smarthttp: Has Stopped " + solon_boot_ver());
        }
    }
}

