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
package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.Utils;
import org.noear.solon.core.util.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * Http 调度器
 *
 * @author noear
 * @since 3.3
 */
public class JdkHttpDispatcher {
    private ExecutorService executorService;

    public ExecutorService getExecutorService() {
        if (executorService == null) {
            Utils.locker().lock();
            try {
                if (executorService == null) {
                    executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                            60, TimeUnit.SECONDS,
                            new SynchronousQueue<>()
                            , new NamedThreadFactory("http-dispatcher").daemon(false));
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return executorService;
    }
}
