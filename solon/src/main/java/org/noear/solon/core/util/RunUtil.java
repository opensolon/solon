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
    /**
     * 并行执行器（一般用于执行简单的定时任务）
     */
    private static ExecutorService parallelExecutor;
    /**
     * 异步执行器（一般用于执行 @Async 注解任务）
     */
    private static ExecutorService asyncExecutor;
    /**
     * 调度执行器（一般用于延时任务）
     */
    private static ScheduledExecutorService scheduledExecutor;

    static {
        parallelExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new NamedThreadFactory("Solon-executor-"));

        int asyncPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        asyncExecutor = new ThreadPoolExecutor(asyncPoolSize, asyncPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory("Solon-asyncExecutor-"));

        int scheduledPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        scheduledExecutor = new ScheduledThreadPoolExecutor(scheduledPoolSize,
                new NamedThreadFactory("Solon-echeduledExecutor-"));
    }

    public static void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        if (scheduledExecutor != null) {
            ScheduledExecutorService old = RunUtil.scheduledExecutor;
            RunUtil.scheduledExecutor = scheduledExecutor;
            old.shutdown();
        }
    }

    /**
     * @deprecated 2.5
     */
    @Deprecated
    public static void setExecutor(ExecutorService executor) {
        setParallelExecutor(executor);
    }

    public static void setParallelExecutor(ExecutorService parallelExecutor) {
        if (parallelExecutor != null) {
            ExecutorService old = RunUtil.parallelExecutor;
            RunUtil.parallelExecutor = parallelExecutor;
            old.shutdown();
        }
    }

    public static void setAsyncExecutor(ExecutorService asyncExecutor) {
        if (asyncExecutor != null) {
            ExecutorService old = RunUtil.asyncExecutor;
            RunUtil.asyncExecutor = asyncExecutor;
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
     * 运行并吃掉异常
     */
    public static void runAndTry(RunnableEx task) {
        try {
            task.run();
        } catch (Throwable e) {
            //略过
        }
    }

    /**
     * 并行执行
     */
    public static Future<?> parallel(Runnable task) {
        return parallelExecutor.submit(task);
    }

    /**
     * 并行执行
     */
    public static <T> Future<T> parallel(Callable<T> task) {
        return parallelExecutor.submit(task);
    }

    /**
     * 异步执行
     */
    public static CompletableFuture<Void> async(Runnable task) {
        return CompletableFuture.runAsync(task, asyncExecutor);
    }

    /**
     * 异步执行
     */
    public static <U> CompletableFuture<U> async(Supplier<U> task) {
        return CompletableFuture.supplyAsync(task, asyncExecutor);
    }

    public static CompletableFuture<Void> asyncAndTry(RunnableEx task) {
        return CompletableFuture.runAsync(()->{
            runAndTry(task);
        }, asyncExecutor);
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

    /**
     * 定时任务
     * */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long millisPeriod) {
        return scheduledExecutor.scheduleAtFixedRate(task, initialDelay, millisPeriod, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时任务
     * */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long millisDelay) {
        return scheduledExecutor.scheduleWithFixedDelay(task, initialDelay, millisDelay, TimeUnit.MILLISECONDS);
    }
}
