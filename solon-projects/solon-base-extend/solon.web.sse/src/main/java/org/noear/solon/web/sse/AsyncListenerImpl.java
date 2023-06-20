package org.noear.solon.web.sse;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class AsyncListenerImpl implements ContextAsyncListener {
    SseEmitterHandler handler;

    public AsyncListenerImpl(SseEmitterHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onStart(Context ctx)  {

    }

    @Override
    public void onComplete(Context ctx)  {
        handler.stop();
    }

    @Override
    public void onTimeout(Context ctx)  {
        handler.stopOnTimeout();
    }

    @Override
    public void onError(Context ctx, Throwable e) {
        handler.stopOnError(e);
    }
}
