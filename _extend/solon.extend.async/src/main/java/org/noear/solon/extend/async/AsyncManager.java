package org.noear.solon.extend.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 异步运行管理器
 *
 * @author noear
 * @since 1.6
 */
public class AsyncManager {
    private static ExecutorService executor;

    static {
        executor = Executors.newCachedThreadPool(new AsyncThreadFactory());
    }

    /**
     * 设置执行器
     */
    public static void setExecutor(ExecutorService executor) {
        if (executor != null) {
            AsyncManager.executor = executor;
        }
    }

    /**
     * 提交运行任务（仅用于内部）
     */
    protected static Future<?> submit(Runnable task) {
        return executor.submit(task);
    }
}
