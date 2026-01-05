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
package org.noear.solon.core.util;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.lang.Internal;

import java.util.concurrent.*;

/**
 * 运行执行器持有者
 *
 * @author noear
 * @since 1.12
 */
@Internal
public class RunHolder {
    /**
     * 异步执行器（一般用于执行 @Async 注解任务）
     */
    private ExecutorService parallelExecutor;
    /**
     * 调度执行器（一般用于延时任务）
     */
    private ScheduledExecutorService scheduledExecutor;


    /**
     * 获取并行执行器
     */
    public ExecutorService getParallelExecutor() {
        if (parallelExecutor == null) {
            Utils.locker().lock();
            try {
                if (parallelExecutor == null) {
                    if (Solon.appIf(app -> app.cfg().isEnabledVirtualThreads())) {
                        parallelExecutor = ThreadsUtil.newVirtualThreadPerTaskExecutor("Solon-executor-");
                    } else {
                        int asyncPoolSize = Runtime.getRuntime().availableProcessors() * 2;
                        parallelExecutor = Executors.newFixedThreadPool(asyncPoolSize,
                                new NamedThreadFactory("Solon-executor-"));
                    }
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return parallelExecutor;
    }

    /**
     * 获取调度执行器
     */
    public ScheduledExecutorService getScheduledExecutor() {
        if (scheduledExecutor == null) {
            Utils.locker().lock();
            try {
                if (scheduledExecutor == null) {
                    if (Solon.appIf(app -> app.cfg().isEnabledVirtualThreads())) {
                        scheduledExecutor = Executors.newScheduledThreadPool(0,
                                ThreadsUtil.newVirtualThreadFactory("Solon-scheduledExecutor-"));
                    } else {
                        int scheduledPoolSize = Runtime.getRuntime().availableProcessors() * 2;
                        scheduledExecutor = Executors.newScheduledThreadPool(scheduledPoolSize,
                                new NamedThreadFactory("Solon-scheduledExecutor-"));
                    }
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return scheduledExecutor;
    }

    /**
     * 设置调度执行器
     */
    public void setScheduledExecutor(ScheduledExecutorService executor) {
        if (executor != null) {
            ScheduledExecutorService old = scheduledExecutor;
            scheduledExecutor = executor;

            if (old != null) {
                old.shutdown();
            }
        }
    }

    /**
     * 设置并行执行器
     */
    public void setParallelExecutor(ExecutorService executor) {
        if (executor != null) {
            ExecutorService old = parallelExecutor;
            parallelExecutor = executor;

            if (old != null) {
                old.shutdown();
            }
        }
    }

    /**
     * 停止
     */
    public void shutdown() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
            scheduledExecutor = null; //置 null 后，获取时可以重新生成
        }

        if (parallelExecutor != null) {
            parallelExecutor.shutdown();
            parallelExecutor = null;
        }
    }
}