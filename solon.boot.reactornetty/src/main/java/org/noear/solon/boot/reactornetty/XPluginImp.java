package org.noear.solon.boot.reactornetty;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.io.Closeable;
import java.io.IOException;

public class XPluginImp implements XPlugin, Closeable {
    DisposableServer _server;
    @Override
    public void start(XApp app) {
        XServerProp.init();

        long time_start = System.currentTimeMillis();

        try {
            System.out.println("oejs.Server:main: Reactornetty 0.9");

            _server = HttpServer.create()
                    .port(app.port())
                    .handle(new HttpRequestHandler())
                    .bindNow();

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        _server.dispose();
    }
}
