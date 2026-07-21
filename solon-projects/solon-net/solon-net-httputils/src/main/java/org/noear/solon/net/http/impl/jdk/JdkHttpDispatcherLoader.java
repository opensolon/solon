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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.NamedThreadFactory;
import org.noear.solon.core.util.ThreadsUtil;
import org.noear.solon.net.http.HttpConfiguration;

import java.util.concurrent.*;

/**
 * Http 异步调度加载器（用于懒加载）
 * <p>这是异步请求的线程池，不是连接池。连接复用依赖 JVM keep-alive。</p>
 *
 * @author noear
 * @since 3.3
 * @since 4.0.4 线程池上限可配置，默认 daemon 线程
 */
public class JdkHttpDispatcherLoader {
    private volatile ExecutorService dispatcher;

    public ExecutorService getDispatcher() {
        if (dispatcher == null) {
            Utils.locker().lock();
            try {
                if (dispatcher == null) {
                    if (Solon.appIf(app -> app.cfg().isEnabledVirtualThreads())) {
                        dispatcher = ThreadsUtil.newVirtualThreadPerTaskExecutor("http-dispatcher-");
                    } else {
                        int maxThreads = Math.max(1, HttpConfiguration.getMaxRequests());
                        int coreThreads = Math.max(2, maxThreads / 4);
                        dispatcher = new ThreadPoolExecutor(coreThreads, maxThreads,
                                60, TimeUnit.SECONDS,
                                new LinkedBlockingQueue<>(64),
                                new NamedThreadFactory("http-dispatcher-").daemon(true),
                                new ThreadPoolExecutor.CallerRunsPolicy());
                    }
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return dispatcher;
    }
}
