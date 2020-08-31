package org.noear.solon.core;

import org.noear.solon.ext.RunnableEx;

/**
 * 事务
 * */
public interface Tran {
    /**
     * 添加从事务
     */
    default void add(Tran slave) {
    }

    /**
     * 应用事务
     */
    void apply(RunnableEx runnable) throws Throwable;
}
