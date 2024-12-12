/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.rx.CompletableEmitter;
import org.reactivestreams.Subscriber;

/**
 * 可完成的发射器实现
 *
 * @author noear
 * @since 2.9
 */
public class CompletableEmitterImpl implements CompletableEmitter {
    private final Subscriber<? super Void> subscriber;

    public CompletableEmitterImpl(Subscriber<? super Void> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onError(Throwable cause) {
        subscriber.onError(cause);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
