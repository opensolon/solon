package org.noear.solon.core;

import org.noear.solon.ext.RunnableEx;

/**
 * 事务
 * */
public interface Tran {

    /**
     * 是否为事务组
     */
    default boolean isGroup() {
        return false;
    }

    /**
     * 添加事务（当前为事务队列时，可添加）
     */
    default void add(Tran slave) {
    }

    /**
     * 应用事务
     */
    void apply(RunnableEx runnable) throws Throwable;
}
