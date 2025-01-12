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

import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RunUtil;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Action 响应式订阅者
 *
 * @author noear
 * @since 2.3
 */
public class ActionReactiveSubscriber implements Subscriber {
    static final Logger log = LoggerFactory.getLogger(ActionReactiveSubscriber.class);

    private Context ctx;
    private Action action;
    private boolean isMultiple;
    private boolean isStreamX;
    private List<Object> list;
    private boolean isFirst;

    public ActionReactiveSubscriber(Context ctx, Action action, boolean isMultiple) {
        this.ctx = ctx;
        this.action = action;
        this.isMultiple = isMultiple;

        if (ctx.contentTypeNew() != null) {
            this.isStreamX = ctx.contentTypeNew().startsWith(MimeType.APPLICATION_X_NDJSON_VALUE);
        } else {
            this.isStreamX = false;
        }

        if (isStreamX == false) {
            this.list = new ArrayList<>();
        }
    }

    private void request(Subscription subscription) {
        if (isMultiple) {
            subscription.request(Long.MAX_VALUE);
        } else {
            subscription.request(1);
        }
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        isFirst = true;

        //启动异步模式（-1 表示不超时）
        if (ctx.asyncStarted()) {
            RunUtil.async(() -> request(subscription));
        } else {
            ctx.asyncStart(-1L, () -> request(subscription));
        }
    }

    @Override
    public void onNext(Object o) {
        try {
            if (isStreamX) {
                try {
                    if (isFirst == false) {
                        ctx.output("\n");
                    }
                    action.render(o, ctx, true);
                } catch (Throwable e) {
                    log.warn(e.getMessage(), e);
                }
            } else {
                list.add(o);
            }
        } finally {
            isFirst = false;
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
            try {
                if (isStreamX == false) {
                    if (isMultiple) {
                        action.render(list, ctx, false);
                    } else {
                        if (list.size() > 0) {
                            action.render(list.get(0), ctx, false);
                        }
                    }
                }
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            } finally {
                ctx.asyncComplete();
            }
        }
    }
}