package org.noear.solon.core;

/**
 * 生命周期
 *
 * @author noear
 * @since 1.5
 */
@FunctionalInterface
public interface Lifecycle {
    /**
     * 开始
     */
    void start() throws Throwable;

    /**
     * 停止
     */
    default void stop() throws Throwable {
    }
}
