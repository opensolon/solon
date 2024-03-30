package org.noear.solon.boot.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;

/**
 * @author noear
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    public static String solon_boot_ver() {
        return "vertx-http/" + Solon.version();
    }

    private Vertx _vertx;
    private HttpServer _server;

    @Override
    public void start(AppContext context) throws Throwable {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        _vertx = Vertx.vertx();
        context.wrapAndPut(Vertx.class, _vertx);

        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        HttpServerProps props = HttpServerProps.getInstance();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();


        _server = _vertx.createHttpServer();
        _server.requestHandler(new VxHttpHandler());
        if (Utils.isNotEmpty(_host)) {
            _server.listen(_port, _host);
        } else {
            _server.listen(_port);
        }

        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);
        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String httpServerUrl = props.buildHttpServerUrl(false);
        LogUtil.global().info("Connector:main: vertx-http: Started ServerConnector@{HTTP/1.1,[http/1.1]}{" + httpServerUrl + "}");
        LogUtil.global().info("Server:main: vertx-http: Started (" + solon_boot_ver() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.close();
        }

        if (_vertx != null) {
            _vertx.close();
            _vertx = null;
        }
    }
}
