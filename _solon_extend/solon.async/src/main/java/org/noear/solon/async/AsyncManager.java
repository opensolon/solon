package org.noear.solon.async;

import java.util.concurrent.Future;

/**
 * 异步运行管理器
 *
 * @author noear
 * @since 1.6
 */
public class AsyncManager {
    private static AsyncExecutor executor = new AsyncExecutorDefault();

    /**
     * 设置执行器
     *
     * @param executor 执行线程池
     */
    public static void setExecutor(AsyncExecutor executor) {
        if (executor != null) {
            AsyncManager.executor = executor;
        }
    }

    /**
     * 提交运行任务（仅用于内部）
     */
    public static Future<?> submit(Runnable task) {
        return executor.submit(task);
    }
}
