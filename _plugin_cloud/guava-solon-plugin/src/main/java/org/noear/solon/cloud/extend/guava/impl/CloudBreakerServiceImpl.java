package org.noear.solon.cloud.extend.guava.impl;

import org.noear.solon.cloud.impl.CloudBreakerServiceSimple;
import org.noear.solon.cloud.model.Entry;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerServiceImpl extends CloudBreakerServiceSimple {
    @Override
    protected Entry create(String name, int value) {
        return new CloudBreakerEntryImpl(value);
    }
}
