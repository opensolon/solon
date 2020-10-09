package org.noear.solon.boot.reactornetty;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

public class XPluginImp implements XPlugin {
    DisposableServer _server = null;

    public static String solon_boot_ver() {
        return "reactor-netty 0.9.1/1.1";
    }

    @Override
    public void start(XApp app) {
        if (app.enableHttp() == false) {
            return;
        }

        XServerProp.init();

        long time_start = System.currentTimeMillis();

        try {
            System.out.println("solon.Server:main: Reactornetty 0.9(reactor-netty)");

            RnHttpHandler handler = new RnHttpHandler();

            //
            // https://projectreactor.io/docs/netty/release/reference/index.html#_starting_and_stopping_2
            //
            _server = HttpServer.create()
                    .compress(true)
                    .protocol(HttpProtocol.HTTP11)
                    .host("localhost")
                    .port(app.port())
                    .handle(handler)
                    .bindNow();

            new Thread(() -> {
                _server.onDispose()
                        .block();
            }).start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: reactor-netty: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: reactor-netty: Started @" + (time_end - time_start) + "ms");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.dispose();
            _server = null;

            System.out.println("solon.Server:main: reactor-netty: Has Stopped " + solon_boot_ver());
        }
    }
}
