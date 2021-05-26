package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class XPluginImp implements Plugin {
    private HttpServer _server = null;

    public static String solon_boot_ver(){
        return "jdk http/"+ Solon.cfg().version();
    }

    @Override
    public  void start(SolonApp app) {
        if (app.enableHttp() == false) {
            return;
        }

        XServerProp.init();

        String _name = app.cfg().get("server.http.name");
        int _port = app.cfg().getInt("server.http.port", 0);
        if (_port < 1) {
            _port = app.port();
        }

        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: Sun.net.HttpServer(jdkhttp)");

        try {
            _server = HttpServer.create(new InetSocketAddress(_port), 0);

            HttpContext context = _server.createContext("/", new JdkHttpContextHandler());
            context.getFilters().add(new ParameterFilter());

            _server.setExecutor(Executors.newCachedThreadPool());
            _server.start();

            app.signalAdd(new SignalSim(_name, _port, "http", SignalType.HTTP));

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: jdkhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: jdkhttp: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop(0);
        _server = null;
        PrintUtil.info("Server:main: jdkhttp: Has Stopped " + solon_boot_ver());
    }
}
