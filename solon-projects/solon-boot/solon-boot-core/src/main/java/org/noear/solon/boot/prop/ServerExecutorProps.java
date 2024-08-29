/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.boot.prop;

import org.noear.solon.core.util.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * 服务执行属性
 *
 * @author noear
 * @since 1.10
 */
public interface ServerExecutorProps {
    boolean isIoBound();

    /**
     * 核心线程数
     */
    int getCoreThreads();

    /**
     * 最大线程数
     */
    int getMaxThreads(boolean bio);

    /**
     * 闲置超时
     */
    long getIdleTimeout();

    /**
     * 闲置超时
     */
    long getIdleTimeoutOrDefault();

    /**
     * 获取一个执行器（Bio 一级执行器）
     */
    default ExecutorService getBioExecutor(String namePrefix) {
        return new ThreadPoolExecutor(getCoreThreads(), getMaxThreads(true),
                60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), //BlockingQueue //SynchronousQueue
                new NamedThreadFactory(namePrefix));
    }
}
