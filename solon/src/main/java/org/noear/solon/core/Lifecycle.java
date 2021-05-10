package org.noear.solon.core;

/**
 * @author noear
 * @since 1.3
 */
@FunctionalInterface
public interface Lifecycle {
    void start();

    default void stop() {
    }

    default boolean isRunning() {
        return false;
    }
}
