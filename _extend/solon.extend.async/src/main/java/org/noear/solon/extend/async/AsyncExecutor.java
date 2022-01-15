package org.noear.solon.extend.async;

import java.util.concurrent.Future;

/**
 * 异步执行器
 *
 * @author noear
 * @since 1.5
 */
@FunctionalInterface
public interface AsyncExecutor {
    /**
     * 提交任务
     * */
    Future<?> submit(Runnable task);
}
