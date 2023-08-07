package org.noear.solon.boot.vertx;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.Solon;

/**
 * @author noear
 * @since 2.3
 */
public class VxHttpHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        HttpServerResponse response = request.response();
        VxHttpContext context = new VxHttpContext(request, response);

        Solon.app().tryHandle(context);
    }
}
