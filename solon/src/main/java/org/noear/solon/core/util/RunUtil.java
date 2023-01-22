package org.noear.solon.core.util;

import java.util.concurrent.*;

/**
 * @author noear
 * @since 1.12
 */
public class RunUtil {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());


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
    public static Future<?> async(Runnable task) {
        return CompletableFuture.runAsync(task);
    }

    /**
     * 延迟执行
     */
    public static ScheduledFuture<?> delay(Runnable task, int seconds) {
        return scheduledExecutor.schedule(task, seconds, TimeUnit.SECONDS);
    }

    /**
     * 延迟执行并重复
     */
    public static ScheduledFuture<?> delayAndRepeat(Runnable task, int seconds) {
        return scheduledExecutor.scheduleWithFixedDelay(task, 1, seconds, TimeUnit.SECONDS);
    }
}
