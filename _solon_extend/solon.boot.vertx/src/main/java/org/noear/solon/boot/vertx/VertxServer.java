package org.noear.solon.boot.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.vertx.http.VertxHttpHandler;

/**
 * @author noear
 * @since 1.8
 */
public class VertxServer implements ServerLifecycle {
    HttpServer server;

    @Override
    public void start(String host, int port) throws Throwable {
        server = Vertx.vertx().createHttpServer();

        server.requestHandler(new VertxHttpHandler());

        if (Utils.isEmpty(host)) {
            server.listen(port);
        } else {
            server.listen(port, host);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.close();
        }
    }
}
