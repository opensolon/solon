package org.noear.solon.cloud.extend.sentinel.impl;

import org.noear.solon.cloud.impl.CloudBreakerServiceLocalImpl;
import org.noear.solon.cloud.model.BreakerEntrySim;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerServiceImpl extends CloudBreakerServiceLocalImpl {
    @Override
    protected BreakerEntrySim create(String name, int value) {
        return new CloudBreakerEntryImpl(name, value);
    }
}
