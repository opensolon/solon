package org.noear.solon.extend.data;

import org.noear.solon.ext.RunnableEx;

/**
 * 事务节点
 * */
public interface TranNode {
    /**
     * 添加从事务
     */
    default void add(TranNode slave) {
    }

    /**
     * 应用事务
     */
    void apply(RunnableEx runnable) throws Throwable;
}
