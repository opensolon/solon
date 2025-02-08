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

import org.reactivestreams.Subscription;

import java.util.function.BiConsumer;

/**
 * 订阅-简单实现
 *
 * @author noear
 * @since 3.1
 */
public class SimpleSubscription implements Subscription {
    private boolean cancelled;
    private BiConsumer<SimpleSubscription, Long> onRequest;

    public SimpleSubscription onRequest(BiConsumer<SimpleSubscription, Long> onRequest) {
        this.onRequest = onRequest;
        return this;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void request(long l) {
        if (onRequest != null) {
            onRequest.accept(this, l);
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}
