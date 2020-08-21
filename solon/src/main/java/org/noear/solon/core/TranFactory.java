package org.noear.solon.core;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

/**
 * 事务工厂
 * */
public interface TranFactory {
    /**
     * 创建事务
     * */
    Tran create(XTran anno);

    /**
     * 挂起运行
     * */
    void pending(RunnableEx runnable) throws Throwable;
}
