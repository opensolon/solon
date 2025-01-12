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
 * 字符串排它锁
 *
 * @author noear
 * @since 2.7
 */
public class StringMutexLock {
    private final ConcurrentMap<String, CountDownLatch> lockKeyHolder = new ConcurrentHashMap<>();

    /**
     * 锁
     */
    public void lock(String lockKey) {
        while (!tryLock(lockKey)) {
            try {
                blockOnSecondLevelLock(lockKey);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 解锁
     */
    public boolean unlock(String lockKey) {
        // 先删除锁，再释放锁，此处会导致后续进来的并发优先执行，无影响
        CountDownLatch realLock = getAndReleaseLock1(lockKey);
        releaseSecondLevelLock(realLock);
        return true;
    }

    /**
     * 尝试锁
     */
    private boolean tryLock(String lockKey) {
        return lockKeyHolder.putIfAbsent(lockKey, new CountDownLatch(1)) == null;
    }

    /**
     * 释放1级锁（删除） 并返回重量级锁
     *
     * @return 真正的锁
     */
    private CountDownLatch getAndReleaseLock1(String lockKey) {
        return lockKeyHolder.remove(lockKey);
    }

    /**
     * 二级锁锁定（锁升级）
     */
    private void blockOnSecondLevelLock(String lockKey) throws InterruptedException {
        CountDownLatch realLock = getRealLockByKey(lockKey);
        // 为 null 说明此时锁已被删除，  next race
        if (realLock != null) {
            realLock.await();
        }
    }

    /**
     * 二级锁解锁（如有必要）
     *
     * @param realLock 锁实例
     */
    private void releaseSecondLevelLock(CountDownLatch realLock) {
        realLock.countDown();
    }

    /**
     * 通过key 获取对应的锁实例
     *
     * @return 锁实例
     */
    private CountDownLatch getRealLockByKey(String lockKey) {
        return lockKeyHolder.get(lockKey);
    }
}