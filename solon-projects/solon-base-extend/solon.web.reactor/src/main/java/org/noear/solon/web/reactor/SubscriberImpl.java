package org.noear.solon.web.reactor;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author noear 2023/6/19 created
 */
public class SubscriberImpl implements Subscriber {
    Context ctx;
    Action action;

    public SubscriberImpl(Context ctx, Action action) {
        this.ctx = ctx;
        this.action = action;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        ctx.asyncStart();
        subscription.request(1L);
    }

    @Override
    public void onNext(Object o) {
        try {
            action.render(o, ctx);
        } catch (Throwable e) {
            EventBus.pushTry(e);
        }
    }

    @Override
    public void onError(Throwable e) {
        try {
            action.render(e, ctx);
        } catch (Throwable e2) {
            EventBus.pushTry(e2);
        }
    }

    @Override
    public void onComplete() {
        ctx.asyncComplete();
    }
}
