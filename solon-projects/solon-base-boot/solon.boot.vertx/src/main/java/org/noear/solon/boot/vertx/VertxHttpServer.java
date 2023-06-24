package org.noear.solon.boot.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

/**
 * @author noear
 * @since 2.3
 */
public class VertxHttpServer extends AbstractVerticle {
    HttpServer server;

    @Override
    public void start() {
        server = vertx.createHttpServer();

        server.requestHandler(new VxHttpHandler());
    }

    @Override
    public void stop() throws Exception {
        if (server != null) {
            server.close();
            server = null;
        }
    }
}
