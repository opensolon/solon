package org.noear.solon.cloud.extend.guava.impl;

import org.noear.solon.cloud.impl.CloudBreakerServiceLocalImpl;
import org.noear.solon.cloud.model.BreakerEntry;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerServiceImpl extends CloudBreakerServiceLocalImpl {
    private static CloudBreakerServiceImpl instance;
    public static synchronized CloudBreakerServiceImpl getInstance() {
        if (instance == null) {
            instance = new CloudBreakerServiceImpl();
        }

        return instance;
    }

    private CloudBreakerServiceImpl(){
        super();
    }


    @Override
    protected BreakerEntry create(String name, int value) {
        return new CloudBreakerEntryImpl(value);
    }
}
