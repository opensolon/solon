package org.noear.solon.core.util;

/**
 * 可异常消费者
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface ConsumerEx<T> {
    void accept(T t) throws Throwable;
}
