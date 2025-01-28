package org.noear.solon.rx.impl;

import org.noear.solon.rx.MamaEmitter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author noear
 * @since 3.1
 */
public class MamaEmitterImpl<T> implements MamaEmitter<T> {
    private final Subscriber<T> subscriber;
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public MamaEmitterImpl(Subscriber<T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onError(Throwable cause) {
        subscriber.onError(cause);
    }

    @Override
    public void onNext(T item) {
        subscriber.onNext(item);
    }

    @Override
    public void onComplete(Publisher<? extends T> publisher) {
        if (completed.compareAndSet(false, true)) {
            publisher.subscribe(subscriber);
        }
    }

    @Override
    public void onComplete() {
        if (completed.compareAndSet(false, true)) {
            subscriber.onComplete();
        }
    }
}
