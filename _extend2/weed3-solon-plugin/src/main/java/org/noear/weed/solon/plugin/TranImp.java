package org.noear.weed.solon.plugin;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbContext;
import org.noear.weed.DbTran;

public class TranImp extends DbTran implements Tran {
    public TranImp(DbContext context) {
        super(context);
    }

    @Override
    public void execute(RunnableEx runnable) throws Exception {
        super.execute((t) -> {
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
