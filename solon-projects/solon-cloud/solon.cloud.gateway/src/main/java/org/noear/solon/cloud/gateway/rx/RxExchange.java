package org.noear.solon.cloud.gateway.rx;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.cloud.gateway.integration.TimeoutProperties;
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.util.KeyValues;

import java.net.URI;
import java.util.Map;

public class RxExchange {
    private HttpServerRequest rawRequest;
    private RxExchangeRequest request;
    private RxExchangeResponse response;
    private URI target;
    private TimeoutProperties timeout;

    public RxExchange(HttpServerRequest rawRequest) {
        this.rawRequest = rawRequest;
    }

    public void bind(Route route) {
        this.target = route.getTarget();
        this.timeout = route.getTimeout();
    }

    public URI target() {
        return target;
    }

    public TimeoutProperties timeout() {
        return timeout;
    }

    public RxExchangeRequest request() {
        if (request == null) {
            request = new RxExchangeRequest();

            request.method(rawRequest.method().name());
            request.queryString(rawRequest.query());
            request.path(rawRequest.path());

            for (Map.Entry<String, String> kv : rawRequest.headers().entries()) {
                request.headerAdd(kv.getKey(), kv.getValue());
            }
        }

        return request;
    }

    public RxExchangeResponse response() {
        if (response == null) {
            response = new RxExchangeResponse();
        }

        return response;
    }

    public void complete() {
        HttpServerResponse rawResponse = rawRequest.response();

        rawResponse.setStatusCode(response().getStatus());

        for (KeyValues<String> kv : response().getHeaders().values()) {
            rawResponse.putHeader(kv.getKey(), kv.getValues());
        }

        if (response().getBody() != null) {
            rawResponse.end(response().getBody());
        } else {
            rawResponse.end();
        }
    }
}
