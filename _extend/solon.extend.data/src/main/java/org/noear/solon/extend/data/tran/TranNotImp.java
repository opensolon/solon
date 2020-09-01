package org.noear.solon.extend.data.tran;


import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranLocal;

public class TranNotImp implements Tran {
    public TranNotImp() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        DbTran tran = TranLocal.trySuspend();

        try {
            runnable.run();
        } finally {
            TranLocal.tryResume(tran);
        }
    }
}
