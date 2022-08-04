package org.noear.solon.core.util;

/**
 * 双参数消费者
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BiConsumerEx<T1,T2> {
    void accept(T1 t1, T2 t2) throws Throwable;
}
