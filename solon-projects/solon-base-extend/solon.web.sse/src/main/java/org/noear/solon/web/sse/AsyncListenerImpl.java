package org.noear.solon.web.sse;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class AsyncListenerImpl implements ContextAsyncListener {
    SseEmitter emitter;

    public AsyncListenerImpl(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onStart(Context ctx) throws IOException {

    }

    @Override
    public void onComplete(Context ctx) throws IOException {
        emitter.internalComplete();
    }

    @Override
    public void onTimeout(Context ctx) throws IOException {
        if (emitter.onTimeout != null) {
            emitter.onTimeout.run();
        }

        emitter.internalComplete();
    }

    @Override
    public void onError(Context ctx, Throwable e) throws IOException {
        if (emitter.onError != null) {
            emitter.onError.accept(e);
        }

        emitter.internalComplete();
    }
}
