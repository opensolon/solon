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
package org.noear.solon.data.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;


/**
 * 字符串排它锁（支持 LockEntry 模式）
 *
 * @author noear
 * @since 2.7
 * @since 3.8
 */
public class StringMutexLock {
    private final ConcurrentMap<String, CountDownLatch> lockKeyHolder = new ConcurrentHashMap<>();

    /**
     * 获取锁入口（支持 try-with-resources）
     */
    public AutoCloseable lockEntry(String lockKey) {
        lock(lockKey);
        return () -> unlock(lockKey);
    }

    /**
     * 加锁
     */
    public void lock(String lockKey) {
        while (true) {
            // 1. 先尝试获取已有的锁，避免频繁 new 对象
            CountDownLatch existing = lockKeyHolder.get(lockKey);
            if (existing != null) {
                try {
                    existing.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Locking interrupted", e);
                }
                continue;
            }

            // 2. 尝试抢占
            CountDownLatch newLatch = new CountDownLatch(1);
            existing = lockKeyHolder.putIfAbsent(lockKey, newLatch);

            if (existing == null) {
                return; // 抢锁成功
            } else {
                // 3. 抢锁失败，直接在别人放进去的锁上等待
                try {
                    existing.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Locking interrupted", e);
                }
            }
        }
    }

    /**
     * 解锁
     */
    public void unlock(String lockKey) {
        // 先删除，再释放，确保后续进来的并发能重新触发 tryLock 流程
        CountDownLatch latch = lockKeyHolder.remove(lockKey);
        if (latch != null) {
            latch.countDown();
        }
    }
}