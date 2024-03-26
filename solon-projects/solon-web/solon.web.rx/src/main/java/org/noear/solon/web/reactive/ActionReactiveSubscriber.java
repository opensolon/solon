package org.noear.solon.web.reactive;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    public ActionReactiveSubscriber(Context ctx, Action action) {
        this.ctx = ctx;
        this.action = action;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        //启动异步模式
        ctx.asyncStart(-1L, null);

        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        try {
            action.render(o, ctx);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(Throwable e) {
        try {
            action.render(e, ctx);
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
                ctx.asyncComplete();
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
