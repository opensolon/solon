package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranMeta;

public class TranDbImp extends DbTran implements Tran {
    public TranDbImp(TranMeta meta) {
        super(meta);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        super.execute(() -> {
            runnable.run();
        });
    }
}
