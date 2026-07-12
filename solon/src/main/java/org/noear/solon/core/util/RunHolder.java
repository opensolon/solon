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
 * <pre>{@code
 * #并行线程池大小（默认：max(cpu*4, 16)，支持固定值 16 或内核倍数 x4）
 * solon.threads.parallelPoolSize: 0
 * #调度线程池大小（默认：cpu*2，支持固定值 8 或内核倍数 x2）
 * solon.threads.scheduledPoolSize: 0
 * }</pre>
 *
 * @author noear
 * @since 1.12
 * @since 4.0
 */
@Internal
public class RunHolder {
    /**
     * 并行线程池大小配置
     */
    private static final String PROP_PARALLEL_POOL_SIZE = "solon.threads.parallelPoolSize";
    /**
     * 调度线程池大小配置
     */
    private static final String PROP_SCHEDULED_POOL_SIZE = "solon.threads.scheduledPoolSize";

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
                        int asyncPoolSize = getParallelPoolSize();
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
                        int scheduledPoolSize = getScheduledPoolSize();
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

    /**
     * 并行线程池大小（默认：max(cpu*4, 16)）
     * <p>
     * 配置：solon.threads.parallelPoolSize，支持固定值 16 或内核倍数 x4
     */
    private int getParallelPoolSize() {
        int poolSize = getPoolSizeOfConfig(PROP_PARALLEL_POOL_SIZE);
        if (poolSize > 0) {
            return poolSize;
        } else {
            return Math.max(getCoreNum() * 4, 16);
        }
    }

    /**
     * 调度线程池大小（默认：cpu*2）
     * <p>
     * 配置：solon.threads.scheduledPoolSize，支持固定值 8 或内核倍数 x2
     */
    private int getScheduledPoolSize() {
        int poolSize = getPoolSizeOfConfig(PROP_SCHEDULED_POOL_SIZE);
        if (poolSize > 0) {
            return poolSize;
        } else {
            return getCoreNum() * 2;
        }
    }

    /**
     * 从配置读取线程池大小（支持：16 或 x16）
     */
    private int getPoolSizeOfConfig(String propName) {
        if (Solon.cfg() == null) {
            return 0;
        }

        //支持：16 或 x16（倍数）
        String poolSizeStr = Solon.cfg().get(propName);
        if (Utils.isNotEmpty(poolSizeStr)) {
            if (poolSizeStr.startsWith("x") || poolSizeStr.startsWith("X")) {
                //倍数模式
                if (poolSizeStr.length() > 1) {
                    return getCoreNum() * Integer.parseInt(poolSizeStr.substring(1));
                } else {
                    return 0;
                }
            } else {
                return Integer.parseInt(poolSizeStr);
            }
        }

        return 0;
    }

    /**
     * Cpu 核数
     */
    private int getCoreNum() {
        return Runtime.getRuntime().availableProcessors();
    }
}
