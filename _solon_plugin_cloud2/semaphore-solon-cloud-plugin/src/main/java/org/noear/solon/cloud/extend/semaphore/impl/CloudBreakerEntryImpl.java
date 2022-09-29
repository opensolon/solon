package org.noear.solon.cloud.extend.semaphore.impl;

import org.noear.solon.cloud.model.BreakerEntrySim;
import org.noear.solon.cloud.model.BreakerException;

import java.util.concurrent.Semaphore;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerEntryImpl extends BreakerEntrySim {
    String breakerName;
    int thresholdValue;
    Semaphore limiter;

    public CloudBreakerEntryImpl(String breakerName, int permits) {
        this.breakerName = breakerName;
        this.thresholdValue = permits;
        this.limiter = new Semaphore(permits);
    }

    @Override
    public AutoCloseable enter() throws BreakerException {
        if (limiter.tryAcquire()) {
            return this;
        } else {
            throw new BreakerException();
        }
    }

    @Override
    public void close() throws Exception {
        limiter.release();
    }

    @Override
    public void reset(int value) {
        if (thresholdValue != value) {
            thresholdValue = value;

            limiter = new Semaphore(value);
        }
    }
}
