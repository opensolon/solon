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
package org.noear.solon.rx;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 订阅者-简单实现
 *
 * @author noear
 * @since 2.9
 */
public class SimpleSubscriber<T> implements Subscriber<T> {
    private Consumer<Subscription> doOnSubscribe;
    private Function<T, Boolean> doOnNextFunc;
    private Consumer<T> doOnNextCons;
    private Consumer<Throwable> doOnError;
    private Runnable doOnComplete;

    private final AtomicBoolean isCompleted = new AtomicBoolean(false);

    public SimpleSubscriber<T> doOnSubscribe(Consumer<Subscription> doOnSubscribe) {
        this.doOnSubscribe = doOnSubscribe;
        return this;
    }

    /**
     * 往下时（可控制取消）
     */
    public SimpleSubscriber<T> doOnNext(Function<T, Boolean> doOnNext) {
        this.doOnNextFunc = doOnNext;
        this.doOnNextCons = null;
        return this;
    }

    public SimpleSubscriber<T> doOnNext(Consumer<T> doOnNext) {
        this.doOnNextFunc = null;
        this.doOnNextCons = doOnNext;
        return this;
    }

    public SimpleSubscriber<T> doOnError(Consumer<Throwable> doOnError) {
        this.doOnError = doOnError;
        return this;
    }

    public SimpleSubscriber<T> doOnComplete(Runnable doOnComplete) {
        this.doOnComplete = doOnComplete;
        return this;
    }

    private Subscription subscription;

    /**
     * 取消
     */
    public void cancel() {
        if (subscription != null) {
            subscription.cancel();
        }
    }


    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;

        if (doOnSubscribe != null) {
            doOnSubscribe.accept(subscription);
        } else {
            subscription.request(1L);
        }
    }

    @Override
    public void onNext(T item) {
        if (doOnNextFunc != null) {
            if (doOnNextFunc.apply(item) == false) {
                cancel();
            }
        } else if (doOnNextCons != null) {
            doOnNextCons.accept(item);
        }

        subscription.request(1L);
    }

    @Override
    public void onError(Throwable throwable) {
        if (doOnError != null) {
            doOnError.accept(throwable);
        }
    }

    @Override
    public void onComplete() {
        if (isCompleted.compareAndSet(false, true)) {
            //确保只运行一次
            if (doOnComplete != null) {
                doOnComplete.run();
            }
        }
    }
}