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

import org.noear.solon.rx.Completable;
import org.noear.solon.rx.CompletableEmitter;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

/**
 * 可完成的发布者实现
 *
 * @author noear
 * @since 2.9
 */
public class CompletableImpl implements Completable, Subscription {
    private final SimpleSubscriber<Object> subscriberBuilder;
    private final Throwable cause;
    private Consumer<CompletableEmitter> emitterConsumer;

    public CompletableImpl(Throwable cause, Consumer<CompletableEmitter> emitterConsumer) {
        this.subscriberBuilder = new SimpleSubscriber<>();
        this.cause = cause;
        this.emitterConsumer = emitterConsumer;
    }

    @Override
    public void subscribe(Subscriber<? super Void> subscriber) {
        //开始订阅
        subscriber.onSubscribe(this);

        if (emitterConsumer == null) {
            if (cause == null) {
                //完成
                subscriber.onComplete();
            } else {
                //出错
                subscriber.onError(cause);
            }
        } else {
            //转发发射器
            emitterConsumer.accept(new CompletableEmitterImpl(subscriber));
        }
    }

    @Override
    public void request(long l) {

    }

    @Override
    public void cancel() {

    }


    @Override
    public Completable doOnError(Consumer<Throwable> doOnError) {
        return Completable.create(emitter -> {
            subscriberBuilder.doOnError(doOnError);
            subscriberBuilder.doOnComplete(() -> {
                emitter.onComplete();
            });

            subscribe();
        });
    }

    @Override
    public Completable doOnComplete(Runnable doOnComplete) {
        return Completable.create(emitter -> {
            subscriberBuilder.doOnError(err -> {
                emitter.onError(err);
            }).doOnComplete(() -> {
                try {
                    doOnComplete.run();
                } finally {
                    emitter.onComplete();
                }
            });
            subscribe();
        });
    }

    @Override
    public Completable then(Completable completable) {
        return Completable.create(emitter -> {
            subscribe(subscriberBuilder.doOnError(err -> {
                emitter.onError(err);
            }).doOnComplete(() -> {
                completable.doOnComplete(() -> {
                    emitter.onComplete();
                }).doOnError(err -> {
                    emitter.onError(err);
                }).subscribe();
            }));
        });
    }

    @Override
    public void subscribe() {
        subscribe(subscriberBuilder);
    }
}