package org.noear.weed.solon.plugin;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbContext;
import org.noear.weed.DbTran;
import org.noear.weed.DbTranQueue;

public class TranImpQueue extends DbTranQueue implements Tran {

    @Override
    public boolean isMaster() {
        return true;
    }

    @Override
    public void add(Tran slave) {
        super.add((DbTran) slave);
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
