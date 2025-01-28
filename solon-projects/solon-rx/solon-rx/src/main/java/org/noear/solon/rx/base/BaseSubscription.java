package org.noear.solon.rx.base;

import org.reactivestreams.Subscription;

/**
 * @author noear
 * @since 3.1
 * */
public class BaseSubscription implements Subscription {
    private long request;
    private boolean cancelled;

    @Override
    public void request(long l) {
        this.request = l;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    public long getRequest() {
        return request;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}