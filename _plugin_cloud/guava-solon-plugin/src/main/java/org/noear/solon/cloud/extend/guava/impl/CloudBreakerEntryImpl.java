package org.noear.solon.cloud.extend.guava.impl;

import com.google.common.util.concurrent.RateLimiter;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.cloud.model.BreakerEntry;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerEntryImpl implements BreakerEntry {
    RateLimiter limiter;

    public CloudBreakerEntryImpl(int permitsPerSecond) {
        limiter = RateLimiter.create(permitsPerSecond);
    }

    @Override
    public AutoCloseable enter() throws BreakerException {
        if (limiter.tryAcquire()) {
            return this;
        } else {
            throw new BreakerException();
        }
    }
}
