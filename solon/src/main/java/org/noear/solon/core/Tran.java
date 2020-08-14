package org.noear.solon.core;

import org.noear.solon.ext.RunnableEx;

/**
 * 事务
 * */
public interface Tran {
    /**
     * 是否为队列事务
     * */
    default boolean isQueue(){ return false; }

    /**
     * 添加事务（当前为事务队列时，可添加）
     * */
    default void add(Tran slave){ }

    /**
     * 执行事务
     * */
    void execute(RunnableEx runnable) throws Throwable;
}
