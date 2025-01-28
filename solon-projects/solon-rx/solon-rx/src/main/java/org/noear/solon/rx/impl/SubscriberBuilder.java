package org.noear.solon.rx.impl;

import org.noear.solon.rx.base.BaseSubscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

/**
 * @author noear
 * @since 3.1
 */
public class SubscriberBuilder<T> implements BaseSubscriber<T> {
    private Consumer<? super Subscription> doOnSubscribe;
    private Consumer<? super T> doOnNext;
    private Consumer<? super Throwable> doOnError;
    private Runnable doOnComplete;

    public SubscriberBuilder doOnSubscribe(Consumer<? super Subscription> doOnSubscribe) {
        this.doOnSubscribe = doOnSubscribe;
        return this;
    }

    public SubscriberBuilder doOnNext(Consumer<? super T> doOnNext) {
        this.doOnNext = doOnNext;
        return this;
    }

    public SubscriberBuilder doOnError(Consumer<? super Throwable> doOnError) {
        this.doOnError = doOnError;
        return this;
    }

    public SubscriberBuilder doOnComplete(Runnable doOnComplete) {
        this.doOnComplete = doOnComplete;
        return this;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        if (doOnSubscribe != null) {
            doOnSubscribe.accept(subscription);
        }
    }

    @Override
    public void onNext(T t) {
        if (doOnNext != null) {
            doOnNext.accept(t);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (doOnError != null) {
            doOnError.accept(throwable);
        }
    }

    @Override
    public void onComplete() {
        if (doOnComplete != null) {
            doOnComplete.run();
        }
    }
}