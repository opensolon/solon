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
    private static ExecutorService executor;
    private static ScheduledExecutorService scheduledExecutor;

    static {
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new NamedThreadFactory("Solon-executor-"));
        scheduledExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                new NamedThreadFactory("Solon-echeduledExecutor-"));
    }

    public static void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        if (scheduledExecutor != null) {
            ScheduledExecutorService old = RunUtil.scheduledExecutor;
            RunUtil.scheduledExecutor = scheduledExecutor;
            old.shutdown();
        }
    }

    public static void setExecutor(ExecutorService executor) {
        if (executor != null) {
            ExecutorService old = RunUtil.executor;
            RunUtil.executor = executor;
            old.shutdown();
        }
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
     * 并行执行
     */
    public static Future<?> parallel(Runnable task) {
        return executor.submit(task);
    }

    /**
     * 并行执行
     */
    public static <T> Future<T> parallel(Callable<T> task) {
        return executor.submit(task);
    }

    /**
     * 异步执行
     */
    public static CompletableFuture<Void> async(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }

    /**
     * 异步执行
     */
    public static <U> CompletableFuture<U> async(Supplier<U> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }

    /**
     * 延迟执行
     */
    public static ScheduledFuture<?> delay(Runnable task, long millis) {
        return scheduledExecutor.schedule(task, millis, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟执行并重复
     */
    public static ScheduledFuture<?> delayAndRepeat(Runnable task, long millis) {
        return scheduledExecutor.scheduleWithFixedDelay(task, 1000, millis, TimeUnit.MILLISECONDS);
    }
}
