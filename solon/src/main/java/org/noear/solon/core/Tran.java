package org.noear.solon.core;

import org.noear.solon.ext.RunnableEx;

/**
 * 事务
 * */
public interface Tran {
    /**
     * 是否为主事务（一主，多从）
     * */
    boolean isMaster();
    /**
     * 添加从事务
     * */
    void add(Tran slave);
    /**
     * 执行事务
     * */
    void execute(RunnableEx runnable);
}
