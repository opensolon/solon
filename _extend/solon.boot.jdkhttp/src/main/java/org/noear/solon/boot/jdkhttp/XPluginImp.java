package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;

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

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: Sun.net.HttpServer(jdkhttp)");

        try {
            _server = HttpServer.create(new InetSocketAddress(app.port()), 0);

            HttpContext context = _server.createContext("/", new JdkHttpContextHandler());
            context.getFilters().add(new ParameterFilter());

            _server.setExecutor(Executors.newCachedThreadPool());
            _server.start();

            app.signalAdd(new SignalSim(app.port(), "http", SignalType.HTTP));

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: jdkhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: jdkhttp: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop(0);
        _server = null;
        System.out.println("solon.Server:main: jdkhttp: Has Stopped " + solon_boot_ver());
    }
}
