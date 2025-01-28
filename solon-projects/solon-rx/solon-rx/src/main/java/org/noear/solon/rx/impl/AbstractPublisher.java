package org.noear.solon.rx.impl;

import org.noear.solon.rx.base.BasePublisher;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author noear
 * @since 3.1
 */
public abstract class AbstractPublisher<T,Self extends BasePublisher> implements BasePublisher<T, Self> {
    private SubscriberBuilder<T> subscriberBuilder;

    protected SubscriberBuilder<T> subscriberBuilder() {
        if (subscriberBuilder == null) {
            subscriberBuilder = new SubscriberBuilder<>();
        }
        return subscriberBuilder;
    }

    @Override
    public Self doOnNext(Consumer<? super T> doOnNext) {
        subscriberBuilder().doOnNext(doOnNext);
        return (Self) this;
    }

    @Override
    public Self doOnError(Consumer<Throwable> doOnError) {
        subscriberBuilder().doOnError(doOnError);
        return (Self) this;
    }

    @Override
    public Self doOnComplete(Runnable doOnComplete) {
        subscriberBuilder().doOnComplete(doOnComplete);
        return (Self) this;
    }

    @Override
    public void subscribe() {
        subscribe(subscriberBuilder());
    }

    private Predicate<? super T> filter;

    protected Predicate<? super T> filter() {
        return filter;
    }

    @Override
    public Self filter(Predicate<? super T> filter) {
        this.filter = filter;
        return (Self) this;
    }
}