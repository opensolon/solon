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
@FunctionalInterface
public interface BreakerEntry extends AutoCloseable {
    /**
     * 进入
     */
    AutoCloseable enter() throws BreakerException;

    /**
     * 自动关闭
     */
    default void close() throws Exception {
    }
}
