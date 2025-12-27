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

import java.util.concurrent.*;

/**
 * Http 调度加载器（用于懒加载）
 *
 * @author noear
 * @since 3.3
 */
public class JdkHttpDispatcherLoader {
    private ExecutorService dispatcher;

    public ExecutorService getDispatcher() {
        if (dispatcher == null) {
            Utils.locker().lock();
            try {
                if (dispatcher == null) {
                    if (Solon.appIf(app -> app.cfg().isEnabledVirtualThreads())) {
                        dispatcher = ThreadsUtil.newVirtualThreadPerTaskExecutor();
                    } else {
                        dispatcher = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                60, TimeUnit.SECONDS,
                                new SynchronousQueue<>()
                                , new NamedThreadFactory("http-dispatcher").daemon(false));
                    }
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return dispatcher;
    }
}