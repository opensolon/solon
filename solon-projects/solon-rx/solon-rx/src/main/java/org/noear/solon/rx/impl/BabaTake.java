package org.noear.solon.rx.impl;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author noear
 * @since 3.1
 */
public class BabaTake<T> extends BabaBase<T> implements Subscriber<T> , Subscription {
    private final Publisher<T> publisher;
    private final long take;


    public BabaTake(Publisher<T> publisher, long take) {
        this.publisher = publisher;
        this.take = take;
    }

    /// //////////////

    private Subscriber<? super T> subscriber;
    private Subscription subscription;
    private long index;

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
        index++;
        if (index == take) {
            subscriber.onNext(t);
            this.cancel();
        }
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