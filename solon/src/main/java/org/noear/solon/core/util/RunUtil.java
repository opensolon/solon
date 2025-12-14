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

import org.noear.solon.Utils;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 运行工具
 *
 * @author noear
 * @since 1.12
 */
public class RunUtil {

    /// ///////
    private static RunHolder runHolder = new RunHolder();

    /**
     * 关闭（再次调用时，可自动恢复）
     */
    public static void shutdown() {
        runHolder.shutdown();
    }


    /**
     * 预热（初始化线程池）
     */
    public static void preheat() {
        runHolder.getParallelExecutor();
        runHolder.getScheduledExecutor();
    }

    /**
     * @deprecated 3.0
     */
    @Deprecated
    public static void setParallelExecutor(ExecutorService parallelExecutor) {
        runHolder.setParallelExecutor(parallelExecutor);
    }

    /**
     * 设置调度执行器
     */
    public static void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        runHolder.setScheduledExecutor(scheduledExecutor);
    }

    /**
     * 设置异步执行器
     */
    public static void setAsyncExecutor(ExecutorService asyncExecutor) {
        runHolder.setParallelExecutor(asyncExecutor);
    }

    /**
     * io 执行器
     */
    public static ExecutorService io() {
        return runHolder.getParallelExecutor();
    }

    /**
     * timer 执行器
     */
    public static ScheduledExecutorService timer() {
        return runHolder.getScheduledExecutor();
    }

    /**
     * 运行或异常
     */
    public static void runOrThrow(RunnableEx task) {
        try {
            task.run();
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 运行并吃掉异常
     */
    public static void runAndTry(RunnableEx task) {
        try {
            task.run();
        } catch (Throwable ignore) {
            //略过
        }
    }

    /**
     * 调用并吃掉异常
     */
    public static <T> T callAndTry(Callable<T> task) {
        try {
            return task.call();
        } catch (Throwable ignore) {
            return null;
            //略过
        }
    }

    /**
     * 并行执行
     *
     * @deprecated 3.0 {@link #async(Runnable)}
     */
    @Deprecated
    public static Future<?> parallel(Runnable task) {
        return runHolder.getParallelExecutor().submit(task);
    }

    /**
     * 并行执行
     *
     * @deprecated 3.0 {@link #async(Runnable)}
     */
    @Deprecated
    public static <T> Future<T> parallel(Callable<T> task) {
        return runHolder.getParallelExecutor().submit(task);
    }

    /**
     * 异步执行
     */
    public static CompletableFuture<Void> async(Runnable task) {
        return CompletableFuture.runAsync(task, runHolder.getParallelExecutor());
    }

    /**
     * 异步执行
     */
    public static <U> CompletableFuture<U> async(Supplier<U> task) {
        return CompletableFuture.supplyAsync(task, runHolder.getParallelExecutor());
    }

    /**
     * 异步执行并吃掉异常
     */
    public static CompletableFuture<Void> asyncAndTry(RunnableEx task) {
        return CompletableFuture.runAsync(() -> {
            runAndTry(task);
        }, runHolder.getParallelExecutor());
    }

    /**
     * 延迟执行
     */
    public static ScheduledFuture<?> delay(Runnable task, long millis) {
        return runHolder.getScheduledExecutor().schedule(task, millis, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟执行并重复
     */
    public static ScheduledFuture<?> delayAndRepeat(Runnable task, long millis) {
        return runHolder.getScheduledExecutor().scheduleWithFixedDelay(task, 1000, millis, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时任务
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long millisPeriod) {
        return runHolder.getScheduledExecutor().scheduleAtFixedRate(task, initialDelay, millisPeriod, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时任务
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long millisDelay) {
        return runHolder.getScheduledExecutor().scheduleWithFixedDelay(task, initialDelay, millisDelay, TimeUnit.MILLISECONDS);
    }
}