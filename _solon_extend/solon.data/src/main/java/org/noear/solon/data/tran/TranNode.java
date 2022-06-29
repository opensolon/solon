package org.noear.solon.data.tran;

import org.noear.solon.ext.RunnableEx;

/**
 * 事务节点
 *
 * @author noear
 * @since 1.0
 * */
public interface TranNode {
    /**
     * 添加孩子事务
     */
    default void add(TranNode slave) {
    }

    /**
     * 应用事务
     */
    void apply(RunnableEx runnable) throws Throwable;
}
