package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranManager;
import org.noear.solon.extend.data.TranMeta;

public class TranDbNewImp extends DbTran implements Tran {

    public TranDbNewImp(TranMeta meta) {
        super(meta);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //尝试挂起事务
        //
        DbTran tran = TranManager.trySuspend();

        try {
            super.execute(() -> {
                runnable.run();
            });
        } finally {
            //尝试恢复事务
            TranManager.tryResume(tran);
        }
    }
}