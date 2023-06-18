package org.noear.solon.boot.smarthttp.http;

import org.smartboot.http.server.HttpRequest;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.3
 */
public class HttpHolder {
    private final HttpRequest request;
    private final CompletableFuture<Object> future;
    private boolean isAsync;

    public HttpHolder(HttpRequest request, CompletableFuture<Object> future) {
        this.request = request;
        this.future = future;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public CompletableFuture<Object> startAsync() {
        isAsync = true;
        return future;
    }

    public void complete() {
        future.complete(this);
    }
}
