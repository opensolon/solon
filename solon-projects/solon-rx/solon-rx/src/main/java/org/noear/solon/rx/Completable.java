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

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.impl.CompletableImpl;
import org.reactivestreams.Publisher;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 可完成发布者（complete | error）
 *
 * @author noear
 * @since 2.0
 */
public interface Completable extends Publisher<Void> {
    /**
     * 出错时
     */
    Completable doOnError(Consumer<Throwable> doOnError);

    /**
     * 出错时恢复一个新流
     *
     * @since 3.7
     */
    Completable doOnErrorResume(Function<Throwable, Completable> doOnError);

    /**
     * 完成时
     */
    Completable doOnComplete(Runnable doOnComplete);

    /// /////////////

    /**
     * 创建
     */
    static Completable create(Consumer<CompletableEmitter> emitterConsumer) {
        if (emitterConsumer == null) {
            throw new IllegalArgumentException("emitterConsumer cannot be null");
        }

        return new CompletableImpl(null, emitterConsumer);
    }

    /**
     * 然后下一个新流
     */
    Completable then(Supplier<Completable> otherSupplier);

    /**
     * 然后下一个新流
     */
    default Completable then(Completable other) {
        return then(() -> other);
    }

    /**
     * 订阅于
     */
    default Completable subscribeOn(Executor executor) {
        return Completable.create(emitter -> {
            executor.execute(() -> {
                subscribe(emitter);
            });
        });
    }

    /**
     * 订阅延时
     */
    default Completable delay(long delay, TimeUnit unit) {
        return Completable.create(emitter -> {
            RunUtil.delay(() -> {
                subscribe(emitter);
            }, unit.toMillis(delay));
        });
    }


    /**
     * 订阅
     */
    void subscribe();

    /**
     * 订阅
     *
     * @param emitter 发射器
     */
    void subscribe(CompletableEmitter emitter);


    /// /////////////

    /**
     * 完成
     */
    static Completable complete() {
        return new CompletableImpl(null, null);
    }

    /**
     * 出错
     */
    static Completable error(Throwable cause) {
        if (cause == null) {
            throw new IllegalArgumentException("cause cannot be null");
        }

        return new CompletableImpl(cause, null);
    }
}