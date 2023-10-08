package org.noear.solon.data.tran.impl;

import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.data.tran.TranManager;

/**
 * 支持当前事务，如果没有事务则报错（不需要入栈）
 *
 * @author noear
 * @since 1.0
 * */
public class TranMandatoryImpl implements TranNode {
    public TranMandatoryImpl() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        if (TranManager.current() == null) {
            //必须要有事务
            throw new RuntimeException("You must have the same source transaction");
        } else {
            runnable.run();
        }
    }
}
