package org.noear.solon.data.tran.impl;

import org.noear.solon.data.tran.TranManager;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.tran.TranNode;

/**
 * 以无事务的方式执行，如果当前有事务则将其挂起（不需要入栈）
 *
 * @author noear
 * @since 1.0
 * */
public class TranNotImpl implements TranNode {
    public TranNotImpl() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        DbTran tran = TranManager.trySuspend();

        try {
            runnable.run();
        } finally {
            TranManager.tryResume(tran);
        }
    }
}
