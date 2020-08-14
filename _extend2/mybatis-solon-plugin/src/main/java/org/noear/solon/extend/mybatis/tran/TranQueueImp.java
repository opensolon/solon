package org.noear.solon.extend.mybatis.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranQueueImp extends DbTranQueue implements Tran {
    protected TranQueueImp() {

    }

    @Override
    public boolean isQueue() {
        return true;
    }

    @Override
    public void add(Tran node) {
        if (node instanceof DbTran) {
            super.add((DbTran) node);
        }
    }

    @Override
    public void execute(RunnableEx runnable) throws Throwable {
        super.execute((tq) -> {
            runnable.run();
        });
    }
}
