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

import org.noear.solon.rx.impl.CompletableImpl;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;

/**
 * 可完成的发布者（complete | error）
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
     * 完成时
     */
    Completable doOnComplete(Runnable doOnComplete);

    /**
     * 订阅
     */
    void subscribe();

    static Completable create(Consumer<CompletableEmitter> emitterConsumer) {
        return new CompletableImpl(null, emitterConsumer);
    }

    static Completable complete() {
        return new CompletableImpl(null, null);
    }

    static Completable error(Throwable cause) {
        return new CompletableImpl(cause, null);
    }
}