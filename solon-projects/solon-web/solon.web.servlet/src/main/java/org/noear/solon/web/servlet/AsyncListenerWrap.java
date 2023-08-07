package org.noear.solon.web.servlet;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class AsyncListenerWrap implements AsyncListener {
    final Context ctx;
    final ContextAsyncListener real;

    public AsyncListenerWrap(Context ctx, ContextAsyncListener real) {
        this.ctx = ctx;
        this.real = real;
    }

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        real.onComplete(ctx);
    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        real.onTimeout(ctx);
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        real.onError(ctx, asyncEvent.getThrowable());
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
        real.onStart(ctx);
    }
}
