package org.noear.solon.boot.reactornetty;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

public class XPluginImp implements Plugin {
    DisposableServer _server = null;

    public static String solon_boot_ver() {
        return "reactor-netty 0.9.1/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        if (app.enableHttp() == false) {
            return;
        }

        XServerProp.init();

        new Thread(() -> {
            start0(app);
        });
    }

    private void start0(SolonApp app) {
        long time_start = System.currentTimeMillis();

        try {
            PrintUtil.info("Server:main: Reactornetty 0.9(reactor-netty)");

            RnHttpHandler handler = new RnHttpHandler();

            //
            // https://projectreactor.io/docs/netty/release/reference/index.html#_starting_and_stopping_2
            //
            _server = HttpServer.create()
                    .compress(true)
                    .protocol(HttpProtocol.HTTP11)
                    .port(app.port())
                    .handle(handler)
                    .bindNow();


            _server.onDispose()
                    .block();

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: reactor-netty: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            PrintUtil.info("Server:main: reactor-netty: Started @" + (time_end - time_start) + "ms");
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.dispose();
            _server = null;

            PrintUtil.info("Server:main: reactor-netty: Has Stopped " + solon_boot_ver());
        }
    }
}
