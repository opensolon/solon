package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.core.TranIsolation;
import org.noear.solon.core.TranSession;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranMeta;

public class TranDbImp extends DbTran implements Tran {
    TranMeta meta;

    public TranDbImp(TranMeta meta, TranSession session) {
        super(session);
        this.meta = meta;
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        super.execute(meta.isolation(), () -> {
            runnable.run();
        });
    }
}
