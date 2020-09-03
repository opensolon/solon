package org.noear.solon.extend.data.trans;


import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.Tran;
import org.noear.solon.extend.data.TranManager;

public class TranNotImp implements Tran {
    public TranNotImp() {

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
