package org.noear.solon.rx.impl;

import org.noear.solon.rx.base.BasePublisher;

import java.util.function.Consumer;

/**
 * @author noear
 * @since 3.1
 */
public abstract class AbstractPublisher<T,R extends BasePublisher> implements BasePublisher<T, R> {
    private SubscriberBuilder<T> subscriberBuilder;

    protected SubscriberBuilder<T> subscriberBuilder() {
        if (subscriberBuilder == null) {
            subscriberBuilder = new SubscriberBuilder<>();
        }
        return subscriberBuilder;
    }

    @Override
    public R doOnNext(Consumer<? super T> doOnNext) {
        subscriberBuilder().doOnNext(doOnNext);
        return (R) this;
    }

    @Override
    public R doOnError(Consumer<Throwable> doOnError) {
        subscriberBuilder().doOnError(doOnError);
        return (R) this;
    }

    @Override
    public R doOnComplete(Runnable doOnComplete) {
        subscriberBuilder().doOnComplete(doOnComplete);
        return (R) this;
    }

    @Override
    public void subscribe() {
        subscribe(subscriberBuilder());
    }
}