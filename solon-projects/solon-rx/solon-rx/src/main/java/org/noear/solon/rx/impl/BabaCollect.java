package org.noear.solon.rx.impl;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/1/28 created
 */
public class BabaCollect<T> extends BabaBase<List<T>> implements Subscriber<T> {
    private final Publisher<T> publisher;

    public BabaCollect(Publisher<T> publisher) {
        this.publisher = publisher;
    }

    private Subscriber<? super List<T>> subscriber;
    private List<T> list;

    @Override
    public void subscribe(Subscriber<? super List<T>> subscriber) {
        this.subscriber = subscriber;
        this.list = new ArrayList<>();
        publisher.subscribe(this);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T t) {
        list.add(t);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onNext(list);
        subscriber.onComplete();
    }
}
