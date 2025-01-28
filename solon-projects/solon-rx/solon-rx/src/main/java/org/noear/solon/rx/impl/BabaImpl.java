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

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.BabaEmitter;
import org.noear.solon.rx.base.BaseSubscription;
import org.reactivestreams.Subscriber;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 可完成的发布者实现
 *
 * @author noear
 * @since 2.9
 * @since 3.1
 */
public class BabaImpl<T> extends BabaBase<T> {
    private final Duration delay;
    private final Throwable cause;
    private final Supplier<T> supplier;
    private final Consumer<BabaEmitter<T>> emitterConsumer;

    public BabaImpl(Duration delay, Throwable cause, Supplier<T> supplier, Consumer<BabaEmitter<T>> emitterConsumer) {
        this.delay = delay;
        this.cause = cause;
        this.supplier = supplier;
        this.emitterConsumer = emitterConsumer;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        if (delay == null) {
            subscribeDo(subscriber);
        } else {
            RunUtil.delay(() -> {
                subscribeDo(subscriber);
            }, delay.toMillis());
        }
    }

    private void subscribeDo(Subscriber<? super T> subscriber) {
        //开始订阅
        BaseSubscription subscription = new BaseSubscription();
        subscriber.onSubscribe(subscription);

        if (supplier != null) {
            //有结果
            subscriber.onNext(supplier.get());
            subscriber.onComplete();
            return;
        }

        if (cause != null) {
            //出错
            subscriber.onError(cause);
            return;
        }


        if (emitterConsumer != null) {
            //转发发射器
            emitterConsumer.accept(new BabaEmitterImpl(subscriber));
            return;
        }

        //最后算完成
        subscriber.onComplete();
    }
}