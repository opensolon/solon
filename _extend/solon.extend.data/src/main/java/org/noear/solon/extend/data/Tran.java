package org.noear.solon.extend.data;

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
