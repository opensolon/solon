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

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
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
public class ActionRxSubscriber implements Subscriber {
    static final Logger log = LoggerFactory.getLogger(ActionRxSubscriber.class);

    private Context ctx;
    private Action action;
    private boolean firstLine;

    public ActionRxSubscriber(Context ctx, Action action) {
        this.ctx = ctx;
        this.action = action;
        this.firstLine = true;
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
            try {
                if (firstLine == false) {
                    ctx.output(CRLF);
                }
                action.render(o, ctx, true);
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            }
        } finally {
            firstLine = false;
        }
    }

    @Override
    public void onError(Throwable e) {
        try {
            action.render(e, ctx, false);
        } catch (Throwable e2) {
            ctx.status(500);
            log.warn(e.getMessage(), e);
        } finally {
            onComplete();
        }
    }

    @Override
    public void onComplete() {
        if (ctx.asyncSupported()) {
            ctx.asyncComplete();
        }
    }
}