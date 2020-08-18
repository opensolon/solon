package org.noear.weed.solon.plugin.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbContext;
import org.noear.weed.DbTran;
import org.noear.weed.DbTranUtil;

public class TranNewImp extends DbTran implements Tran {
    protected TranNewImp(DbContext context) {
        super(context);
    }

    @Override
    public void execute(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        DbTran tran = DbTranUtil.current();
        try {
            //移除事务
            DbTranUtil.currentRemove();

            super.execute((t) -> {
                runnable.run();
            });
        } finally {
            if (tran != null) {
                //把事务重新放回去
                //
                DbTranUtil.currentSet(tran);
            }
        }
    }
}
