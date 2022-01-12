package org.noear.solon.extend.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步运行管理器
 *
 * @author noear
 * @since 1.6
 */
public class AsyncManager {
    private static ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 设置执行器
     */
    public static void setExecutor(ExecutorService executor) {
        if (executor != null) {
            AsyncManager.executor = executor;
        }
    }

    /**
     * 执行（仅用于内部）
     */
    protected static void execute(Runnable command) {
        executor.submit(command);
    }
}
