package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class XPluginImp implements XPlugin {
    private HttpServer _server = null;

    public static String solon_boot_ver(){
        return "jdk http jdk8/1.0.10.1";
    }

    @Override
    public  void start(XApp app) {
        if (app.enableHttp == false) {
            return;
        }

        XServerProp.init();

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: Sun.net.HttpServer jdk8");

        try {
            _server = HttpServer.create(new InetSocketAddress(app.port()), 0);

            HttpContext context = _server.createContext("/", new JdkHttpContextHandler());
            context.getFilters().add(new ParameterFilter());

            _server.setExecutor(Executors.newCachedThreadPool());
            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
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
        System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
    }
}
