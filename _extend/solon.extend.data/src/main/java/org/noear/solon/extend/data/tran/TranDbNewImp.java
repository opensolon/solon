package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.core.TranSession;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranLocal;
import org.noear.solon.extend.data.TranMeta;

public class TranDbNewImp extends DbTran implements Tran {
    TranMeta meta;

    public TranDbNewImp(TranMeta meta, TranSession session) {
        super(session);
        this.meta = meta;
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //尝试挂起事务
        //
        DbTran tran = TranLocal.trySuspend();

        try {
            super.execute(meta.isolation(), () -> {
                runnable.run();
            });
        } finally {
            //尝试恢复事务
            TranLocal.tryResume(tran);
        }
    }
}