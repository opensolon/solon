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
package org.noear.solon.web.rx.integration;

import org.noear.solon.core.handle.Context;
import org.noear.solon.rx.CompletableEmitter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action 响应式订阅者
 *
 * @author noear
 * @since 2.3
 */
public class RxSubscriberImpl implements Subscriber {
    static final Logger log = LoggerFactory.getLogger(RxSubscriberImpl.class);

    private Context ctx;
    private boolean isStreaming;
    private CompletableEmitter completableEmitter;

    public RxSubscriberImpl(Context ctx, boolean isStreaming, CompletableEmitter completableEmitter) {
        this.ctx = ctx;
        this.isStreaming = isStreaming;
        this.completableEmitter = completableEmitter;
    }

    private void request(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        if (ctx.asyncStarted()) {
            //如果已是异步
            request(subscription);
        } else {
            //如果不是，启动异步模式（-1 表示不超时）
            ctx.asyncStart(-1L, () -> request(subscription));
        }
    }

    private static final byte[] CRLF = "\n".getBytes();

    @Override
    public void onNext(Object o) {
        try {
            ctx.render(o);

            if (isStreaming) {
                ctx.output(CRLF);
                ctx.flush(); //流式输出，每次都要刷一下（避免缓存未输出）
            }
        } catch (Throwable e) {
            onError(e);
        }
    }

    @Override
    public void onError(Throwable e) {
        completableEmitter.onError(e);
    }

    @Override
    public void onComplete() {
        completableEmitter.onComplete();
    }
}