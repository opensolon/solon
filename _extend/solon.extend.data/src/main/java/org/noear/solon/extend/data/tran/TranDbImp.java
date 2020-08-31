package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.core.TranSessionFactory;
import org.noear.solon.ext.RunnableEx;

public class TranDbImp extends DbTran implements Tran {
    public TranDbImp(TranSessionFactory factory){
        super(factory);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        super.execute(() -> {
            runnable.run();
        });
    }
}
