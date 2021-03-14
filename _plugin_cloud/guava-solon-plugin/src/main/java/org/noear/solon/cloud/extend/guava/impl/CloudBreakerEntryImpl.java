package org.noear.solon.cloud.extend.guava.impl;

import com.google.common.util.concurrent.RateLimiter;
import org.noear.solon.cloud.model.Entry;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerEntryImpl implements Entry {
    RateLimiter limiter;

    public CloudBreakerEntryImpl(int permitsPerSecond) {
        limiter = RateLimiter.create(permitsPerSecond);
    }

    @Override
    public void enter(int count) throws InterruptedException {
        if (limiter.tryAcquire(count)) {
            return;
        } else {
            throw new InterruptedException();
        }
    }

    @Override
    public void exit(int count) {

    }
}
