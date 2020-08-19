package org.noear.weed.solon.plugin.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbContext;
import org.noear.weed.DbTran;
import org.noear.weed.DbTranQueue;

public class TranGroupImp extends DbTranQueue implements Tran {
    protected TranGroupImp() {

    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public void add(Tran node) {
        if (node instanceof DbTran) {
            ((DbTran) node).join(this);
        }
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        super.execute((tq) -> {
            runnable.run();
        });
    }
}
