package org.noear.solon.rx.impl;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

/**
 * @author noear
 * @since 3.1
 */
public class BabaFlatMap<T,R> extends BabaBase<R> implements Subscriber<T> , Subscription {
    private final Publisher<T> publisher;
    private final Function<? super T, ? extends Publisher<? extends R>> mapper;


    public BabaFlatMap(Publisher<T> publisher, Function<? super T, ? extends Publisher<? extends R>> mapper) {
        this.publisher = publisher;
        this.mapper = mapper;
    }

    /// //////////////

    private Subscriber<? super R> subscriber;
    private Subscription subscription;
    private boolean hasNext = false;

    @Override
    public void subscribe(Subscriber<? super R> subscriber) {
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
        hasNext = true;

        try {
            Publisher<? extends R> r = mapper.apply(t);
            r.subscribe(subscriber);
        } catch (Throwable err) {
            onError(err);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        if (hasNext == false) {
            subscriber.onComplete();
        }
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