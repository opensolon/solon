package org.noear.solon.core;

/**
 * 生命周期
 *
 * @author noear
 * @since 1.5
 */
public interface Lifecycle {
    default void start(){}
    default void stop(){}
}
