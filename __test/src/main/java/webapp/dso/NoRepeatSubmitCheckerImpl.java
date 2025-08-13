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
package webapp.dso;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.validation.annotation.NoRepeatSubmit;
import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author noear 2021/6/12 created
 */
@Managed
public class NoRepeatSubmitCheckerImpl implements NoRepeatSubmitChecker {

    @Inject
    CacheService cache;

    final ReentrantLock SYNC_LOCK = new ReentrantLock();

    @Override
    public boolean check(NoRepeatSubmit anno, Context ctx, String submitHash, int limitSeconds) {
        String key2 = "lock." + submitHash;

        SYNC_LOCK.lock();
        try {
            Object tmp = cache.get(key2, Object.class);

            if (tmp == null) {
                //如果还没有，则锁成功
                cache.store(key2, "_", limitSeconds);
                return true;
            } else {
                return false;
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }
}
