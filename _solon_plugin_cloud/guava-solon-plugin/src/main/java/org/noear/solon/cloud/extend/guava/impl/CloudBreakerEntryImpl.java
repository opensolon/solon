package org.noear.solon.cloud.extend.guava.impl;

import com.google.common.util.concurrent.RateLimiter;
import org.noear.solon.cloud.model.BreakerEntrySim;
import org.noear.solon.cloud.model.BreakerException;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerEntryImpl extends BreakerEntrySim {
    RateLimiter limiter;
    int thresholdValue;

    public CloudBreakerEntryImpl(int permitsPerSecond) {
        this.thresholdValue = permitsPerSecond;

        loadRules();
    }

    private void loadRules(){
        limiter = RateLimiter.create(thresholdValue);
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
    public void reset(int value) {
        if(thresholdValue != value){
            thresholdValue = value;

            loadRules();
        }
    }
}
