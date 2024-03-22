package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认负载策略
 *
 * @author noear
 * @since 2.2
 */
public class CloudLoadStrategyDefault implements CloudLoadStrategy {
    private static final int indexMax = 9999_9999;
    private static final ReentrantLock SYNC_LOCK = new ReentrantLock();

    @Override
    public String getServer(Discovery discovery) {
        Instance instance;

        SYNC_LOCK.lock();
        try {
            Integer index = discovery.attachment();
            if (index == null || index > indexMax) {
                index = 0;
            }

            instance = discovery.instanceGet(index++);
            discovery.attachmentSet(index);
        } finally {
            SYNC_LOCK.unlock();
        }

        return instance.uri();
    }
}
