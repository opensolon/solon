package org.noear.solon.boot.vertx.http;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author noear
 * @since 1.8
 */
public class VertxHttpHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // This handler gets called for each request that arrives on the
        // server
        HttpServerResponse response = request.response();
        response.putHeader("content-type", "text/plain");

        // Write to the response and end it
        response.end("Hello vertx!");
    }
}
