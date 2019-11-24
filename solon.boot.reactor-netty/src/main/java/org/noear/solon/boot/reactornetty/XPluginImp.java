package org.noear.solon.boot.reactornetty;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

import java.io.Closeable;
import java.io.IOException;

public class XPluginImp implements XPlugin {
    DisposableServer _server;

    public static String solon_boot_ver() {
        return "reactor-netty 0.9.1/1.0.3-b1";
    }

    @Override
    public void start(XApp app) {
        XServerProp.init();

        long time_start = System.currentTimeMillis();

        try {
            System.out.println("solon.Server:main: Reactornetty 0.9");

            HttpRequestHandler handler = new HttpRequestHandler();

            _server = HttpServer.create()
                    .compress(true)
                    .protocol(HttpProtocol.HTTP11)
                    .host("localhost")
                    .port(app.port())
                    .handle(handler)
                    .bindNow();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.onDispose()
                    .block();
            _server = null;
        }
    }
}
