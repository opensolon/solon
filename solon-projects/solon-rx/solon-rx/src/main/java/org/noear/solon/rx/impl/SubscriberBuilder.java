/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.rx.impl;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

/**
 * 订阅者-简单实现
 *
 * @author noear
 * @since 2.9
 */
public class SubscriberBuilder<T> implements Subscriber<T> {
    private Consumer<Long> doOnSubscribe;
    private Consumer<T> doOnNext;
    private Consumer<Throwable> doOnError;
    private Runnable doOnComplete;

    public SubscriberBuilder<T> doOnSubscribe(Consumer<Long> doOnSubscribe) {
        this.doOnSubscribe = doOnSubscribe;
        return this;
    }

    public SubscriberBuilder<T> doOnNext(Consumer<T> doOnNext) {
        this.doOnNext = doOnNext;
        return this;
    }

    public SubscriberBuilder<T> doOnError(Consumer<Throwable> doOnError) {
        this.doOnError = doOnError;
        return this;
    }

    public SubscriberBuilder<T> doOnComplete(Runnable doOnComplete) {
        this.doOnComplete = doOnComplete;
        return this;
    }


    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);

        if (doOnSubscribe != null) {
            subscription.request(Long.MAX_VALUE);
        }
    }

    @Override
    public void onNext(T item) {
        if (doOnNext != null) {
            doOnNext.accept(item);
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