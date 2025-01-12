/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.lang.Nullable;

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

    @Nullable
    @Override
    public String getServer(Discovery discovery, int port) {
        Instance instance;

        SYNC_LOCK.lock();
        try {
            Integer index = discovery.attachment();
            if (index == null || index > indexMax) {
                index = 0;
            }

            instance = discovery.instanceGet(index++, port);
            discovery.attachmentSet(index);
        } finally {
            SYNC_LOCK.unlock();
        }

        if (instance == null) {
            return null;
        } else {
            return instance.uri();
        }
    }
}
