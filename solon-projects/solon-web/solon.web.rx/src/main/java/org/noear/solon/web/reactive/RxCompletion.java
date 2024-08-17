package org.noear.solon.web.reactive;

import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应式完成器
 *
 * @author noear
 * @since 2.9
 */
public class RxCompletion implements Subscriber {
    static final Logger log = LoggerFactory.getLogger(RxCompletion.class);

    private Context ctx;

    public RxCompletion(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        //不管
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            if (throwable instanceof StatusException) {
                StatusException status = (StatusException) throwable;
                ctx.status(status.getCode());

                if (status.getCode() == 404) {
                    return;
                }
            } else {
                ctx.status(500);
            }

            log.error(throwable.getMessage(), throwable);
        } finally {
            this.ctx.asyncComplete();
        }

    }

    @Override
    public void onComplete() {
        this.ctx.asyncComplete();
    }
}
