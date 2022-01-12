package org.noear.solon.extend.async;

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
    void submit(Runnable task);
}
