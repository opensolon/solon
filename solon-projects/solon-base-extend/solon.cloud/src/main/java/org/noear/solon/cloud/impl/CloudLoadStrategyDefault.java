package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

/**
 * 默认负载策略
 *
 * @author noear
 * @since 2.2
 */
public class CloudLoadStrategyDefault implements CloudLoadStrategy {
    private static final int indexMax = 99999999;

    @Override
    public String getServer(Discovery discovery) {
        Instance instance;

        synchronized (discovery.service().intern()) {
            Integer index = discovery.attachment();
            if (index == null || index > indexMax) {
                index = 0;
            }

            instance = discovery.instanceGet(index++);
            discovery.attachmentSet(index);
        }

        return instance.uri();
    }
}
