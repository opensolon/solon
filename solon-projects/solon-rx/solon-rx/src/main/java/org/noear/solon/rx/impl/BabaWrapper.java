package org.noear.solon.rx.impl;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author noear 2025/1/28 created
 */
public class BabaWrapper<T> extends BabaBase<T> implements Subscriber<T>, Subscription {
    private final Publisher<T> publisher;

    public BabaWrapper(Publisher<T> publisher) {
        this.publisher = publisher;
    }

    private Subscriber<? super T> subscriber;
    private Subscription subscription;

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        publisher.subscribe(this);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscriber.onSubscribe(this);
    }

    @Override
    public void onNext(T t) {
        subscriber.onNext(t);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    @Override
    public void request(long l) {
        //只取一个
        subscription.request(1L);
    }

    @Override
    public void cancel() {
        subscription.cancel();
    }
}
