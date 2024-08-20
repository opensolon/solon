package org.noear.solon.rx.impl;

import org.noear.solon.rx.CompletableEmitter;
import org.reactivestreams.Subscriber;

/**
 * 可完成的发射器实现
 *
 * @author noear
 * @since 2.9
 */
public class CompletableEmitterImpl implements CompletableEmitter {
    private final Subscriber<? super Void> subscriber;

    public CompletableEmitterImpl(Subscriber<? super Void> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onError(Throwable cause) {
        subscriber.onError(cause);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
