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
import org.noear.solon.core.bean.LifecycleBean;

import java.util.concurrent.*;

/**
 * 运行执行器持有者
 *
 * @author noear
 * @since 1.12
 */
public class RunHolder {
    /**
     * 异步执行器（一般用于执行 @Async 注解任务）
     */
    private ExecutorService asyncExecutor;
    /**
     * 调度执行器（一般用于延时任务）
     */
    private ScheduledExecutorService scheduledExecutor;


    /**
     * 获取异步执行器
     */
    public ExecutorService getAsyncExecutor() {
        if (asyncExecutor == null) {
            Utils.locker().lock();
            try {
                if (asyncExecutor == null) {
                    if (Solon.appIf(app -> app.cfg().isEnabledVirtualThreads())) {
                        asyncExecutor = ThreadsUtil.newVirtualThreadPerTaskExecutor();
                    } else {
                        int asyncPoolSize = Runtime.getRuntime().availableProcessors() * 2;
                        asyncExecutor = new ThreadPoolExecutor(0, asyncPoolSize,
                                60L, TimeUnit.SECONDS,
                                new LinkedBlockingQueue<Runnable>(),
                                new NamedThreadFactory("Solon-executor-"));
                    }
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return asyncExecutor;
    }

    /**
     * 获取调度执行器
     */
    public ScheduledExecutorService getScheduledExecutor() {
        if (scheduledExecutor == null) {
            Utils.locker().lock();
            try {
                if (scheduledExecutor == null) {
                    int scheduledPoolSize = Runtime.getRuntime().availableProcessors() * 2;
                    scheduledExecutor = new ScheduledThreadPoolExecutor(scheduledPoolSize,
                            new NamedThreadFactory("Solon-scheduledExecutor-"));
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
     * 设置异步执行器
     */
    public void setAsyncExecutor(ExecutorService executor) {
        if (executor != null) {
            ExecutorService old = asyncExecutor;
            asyncExecutor = executor;

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

        if (asyncExecutor != null) {
            asyncExecutor.shutdown();
            asyncExecutor = null;
        }
    }
}