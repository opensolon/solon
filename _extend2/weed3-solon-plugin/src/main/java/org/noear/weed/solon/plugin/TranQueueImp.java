package org.noear.weed.solon.plugin;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbContext;
import org.noear.weed.DbTran;
import org.noear.weed.DbTranQueue;

public class TranQueueImp extends DbTranQueue implements Tran {
    protected TranQueueImp(){

    }

    @Override
    public boolean isQueue() {
        return true;
    }

    @Override
    public void add(Tran node) {
        if(node instanceof DbTran) {
            super.add((DbTran) node);
        }
    }

    @Override
    public void execute(RunnableEx runnable) throws Exception {
        super.execute((tq) -> {
            try {
                runnable.run();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
