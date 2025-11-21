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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 可完成发布者实现
 *
 * @author noear
 * @since 2.9
 * @since 3.7
 */
public class CompletableImpl implements Completable, Subscription {
    private final Throwable cause;
    private volatile Consumer<CompletableEmitter> emitterConsumer;
    private volatile boolean subscribed = false; // 添加订阅状态
    private volatile boolean cancelled = false; // 添加取消状态

    public CompletableImpl(Throwable cause, Consumer<CompletableEmitter> emitterConsumer) {
        this.cause = cause;
        this.emitterConsumer = emitterConsumer;
    }

    @Override
    public void subscribe(Subscriber<? super Void> subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }

        if (cancelled) {
            throw new IllegalStateException("Completable has been cancelled");
        }

        if (subscribed) {
            throw new IllegalStateException("Completable can only be subscribed once");
        } else {
            subscribed = true;
        }

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
        cancelled = true;
        emitterConsumer = null; // 帮助GC
    }


    @Override
    public Completable doOnError(Consumer<Throwable> doOnError) {
        if (doOnError == null) {
            throw new IllegalArgumentException("doOnError consumer cannot be null");
        }

        return Completable.create(emitter -> {
            subscribe(new SimpleSubscriber<Void>() {
                @Override
                public void onError(Throwable err) {
                    try {
                        doOnError.accept(err);
                    } finally {
                        emitter.onError(err);
                    }
                }

                @Override
                public void onComplete() {
                    emitter.onComplete();
                }
            });
        });
    }

    @Override
    public Completable doOnErrorResume(Function<Throwable, Completable> doOnError) {
        if (doOnError == null) {
            throw new IllegalArgumentException("doOnErrorResume function cannot be null");
        }

        return Completable.create(emitter -> {
            subscribe(new SimpleSubscriber<Void>() {
                @Override
                public void onError(Throwable err) {
                    try {
                        Completable resumed = doOnError.apply(err);

                        if (resumed == null) {
                            emitter.onError(err);
                        } else {
                            resumed.subscribe(new SimpleSubscriber<Void>() {
                                @Override
                                public void onComplete() {
                                    emitter.onComplete();
                                }

                                @Override
                                public void onError(Throwable err2) {
                                    emitter.onError(err2);
                                }
                            });
                        }
                    } catch (Throwable t) {
                        emitter.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    emitter.onComplete();
                }
            });
        });
    }

    @Override
    public Completable doOnComplete(Runnable doOnComplete) {
        if (doOnComplete == null) {
            throw new IllegalArgumentException("doOnComplete runnable cannot be null");
        }

        return Completable.create(emitter -> {
            subscribe(new SimpleSubscriber<Void>() {
                @Override
                public void onError(Throwable err) {
                    emitter.onError(err);
                }

                @Override
                public void onComplete() {
                    try {
                        doOnComplete.run();
                    } finally {
                        emitter.onComplete();
                    }
                }
            });
        });
    }

    @Override
    public Completable then(Supplier<Completable> otherSupplier) {
        if (otherSupplier == null) {
            throw new IllegalArgumentException("otherSupplier cannot be null");
        }

        return Completable.create(emitter -> {
            subscribe(new SimpleSubscriber<Void>() {
                @Override
                public void onError(Throwable err) {
                    emitter.onError(err);
                }

                @Override
                public void onComplete() {
                    try {
                        Completable other = otherSupplier.get();
                        if (other == null) {
                            emitter.onComplete();
                        } else {
                            other.subscribe(new SimpleSubscriber<Void>() {
                                @Override
                                public void onComplete() {
                                    emitter.onComplete();
                                }

                                @Override
                                public void onError(Throwable err2) {
                                    emitter.onError(err2);
                                }
                            });
                        }
                    } catch (Throwable t) {
                        emitter.onError(t);
                    }
                }
            });
        });
    }

    @Override
    public void subscribe() {
        subscribe(new SimpleSubscriber<>());
    }

    @Override
    public void subscribe(CompletableEmitter emitter) {
        subscribe(new SimpleSubscriber<Void>() {
            @Override
            public void onError(Throwable err) {
                emitter.onError(err);
            }

            @Override
            public void onComplete() {
                emitter.onComplete();
            }
        });
    }
}