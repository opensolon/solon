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

import org.noear.solon.rx.BabaEmitter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 可完成的发射器实现
 *
 * @author noear
 * @since 2.9
 * @since 3.1
 */
public class BabaEmitterImpl<T> implements BabaEmitter<T> {
    private final Subscriber<T> subscriber;
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public BabaEmitterImpl(Subscriber<T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onError(Throwable cause) {
        subscriber.onError(cause);
    }

    @Override
    public void onComplete(Publisher<? extends T> publisher) {
        if (completed.compareAndSet(false, true)) {
            publisher.subscribe(subscriber);
        }
    }

    @Override
    public void onComplete(T result) {
        if (completed.compareAndSet(false, true)) {
            subscriber.onNext(result);
            subscriber.onComplete();
        }
    }

    @Override
    public void onComplete() {
        if (completed.compareAndSet(false, true)) {
            subscriber.onComplete();
        }
    }
}
