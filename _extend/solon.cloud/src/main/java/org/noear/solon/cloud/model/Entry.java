package org.noear.solon.cloud.model;

/**
 * 断路器入口模型
 *
 * <p><code>
 *     try(Entry entry = CloudClient.breaker("test")){
 *         //业务处理
 *     }
 * </code></p>
 *
 * @author noear
 * @since 1.2
 */
public interface Entry extends AutoCloseable {
    /**
     * 进入
     *
     * @param count 数量
     */
    void enter(int count) throws InterruptedException;

    /**
     * 进入
     */
    default void enter() throws InterruptedException {
        enter(1);
    }

    /**
     * 退出
     *
     * @param count 数量
     */
    void exit(int count);

    /**
     * 退出
     */
    default void exit() {
        exit(1);
    }

    /**
     * 自动关闭
     */
    default void close() throws Exception {
        exit();
    }
}
