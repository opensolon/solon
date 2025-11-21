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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 订阅者-简单实现
 *
 * @author noear
 * @since 2.9
 * @since 3.7
 */
public class SimpleSubscriber<T> implements Subscriber<T> {
    static final Logger log = LoggerFactory.getLogger(SimpleSubscriber.class);

    private Consumer<Subscription> doOnSubscribe;
    private Function<T, Boolean> doOnNextFunc;
    private Consumer<T> doOnNextCons;
    private Consumer<Throwable> doOnError;
    private Runnable doOnComplete;

    private final AtomicBoolean terminated = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

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

    public boolean isCancelled() {
        return cancelled.get();
    }

    /**
     * 取消
     */
    public void cancel() {
        if (cancelled.compareAndSet(false, true) && subscription != null) {
            subscription.cancel();
        }
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;

        if (doOnSubscribe != null) {
            doOnSubscribe.accept(subscription);
        } else {
            subscription.request(Long.MAX_VALUE);
        }
    }

    @Override
    public void onNext(T item) {
        if (terminated.get() || cancelled.get()) {
            return;
        }

        try {
            if (doOnNextFunc != null) {
                if (doOnNextFunc.apply(item) == false) {
                    cancel();
                }
            } else if (doOnNextCons != null) {
                doOnNextCons.accept(item);
            }
        } catch (Throwable t) {
            onError(t); // 将异常转换为 onError
        }
    }

    @Override
    public void onError(Throwable err) {
        if (terminated.compareAndSet(false, true)) {
            if (doOnError != null) {
                try {
                    doOnError.accept(err);
                } catch (Throwable t) {
                    log.warn("Error in doOnError callback", t);
                }
            }
        }
    }

    @Override
    public void onComplete() {
        if (terminated.compareAndSet(false, true)) {
            if (doOnComplete != null) {
                try {
                    doOnComplete.run();
                } catch (Throwable t) {
                    log.warn("Error in doOnComplete callback", t);
                }
            }
        }
    }
}