package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.noear.solon.Solon;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.core.*;
import org.noear.solon.core.util.PrintUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class XPluginImp implements Plugin {
    private static Signal _signal;
    public static Signal signal(){
        return _signal;
    }

    private HttpServer _server = null;

    public static String solon_boot_ver(){
        return "jdk http/"+ Solon.cfg().version();
    }

    @Override
    public  void start(AopContext context) {
        if (Solon.global().enableHttp() == false) {
            return;
        }

        String _name = Solon.cfg().get(ServerConstants.SERVER_HTTP_NAME);
        int _port = Solon.cfg().getInt(ServerConstants.SERVER_HTTP_PORT, 0);
        if (_port < 1) {
            _port = Solon.global().port();
        }

        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: Sun.net.HttpServer(jdkhttp)");

        try {
            _server = HttpServer.create(new InetSocketAddress(_port), 0);

            HttpContext httpContext = _server.createContext("/", new JdkHttpContextHandler());
            httpContext.getFilters().add(new ParameterFilter());

            _server.setExecutor(Executors.newCachedThreadPool());
            _server.start();

            _signal = new SignalSim(_name, _port, "http", SignalType.HTTP);
            Solon.global().signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: jdkhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + _port + "}");
            PrintUtil.info("Server:main: jdkhttp: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
