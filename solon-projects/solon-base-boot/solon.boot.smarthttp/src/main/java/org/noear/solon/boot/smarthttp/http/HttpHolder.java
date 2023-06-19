package org.noear.solon.boot.smarthttp.http;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.smartboot.http.server.HttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.3
 */
public class HttpHolder {
    private final HttpRequest request;
    private final CompletableFuture<Object> future;
    private Context context;
    private boolean isAsync;
    private List<ContextAsyncListener> listeners = new ArrayList<>();

    public HttpHolder(HttpRequest request, CompletableFuture<Object> future) {
        this.request = request;
        this.future = future;
    }

    public void addListener(ContextAsyncListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    public List<ContextAsyncListener> getListeners() {
        return listeners;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
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
