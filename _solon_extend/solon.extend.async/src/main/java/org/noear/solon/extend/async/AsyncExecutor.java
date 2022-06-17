package org.noear.solon.extend.async;

import java.util.concurrent.Future;

/**
 * 异步执行器
 *
 * @author noear
 * @since 1.5
 */
@Deprecated
@FunctionalInterface
public interface AsyncExecutor {
    /**
     * 提交任务
     * */
    Future<?> submit(Runnable task);
}
