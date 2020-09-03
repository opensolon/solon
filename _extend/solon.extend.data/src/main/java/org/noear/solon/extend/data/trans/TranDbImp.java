package org.noear.solon.extend.data.trans;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranNode;

public class TranDbImp extends DbTran implements TranNode {
    public TranDbImp(XTran meta) {
        super(meta);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        super.execute(() -> {
            runnable.run();
        });
    }
}
