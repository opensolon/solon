package org.noear.weed.solon.plugin;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbTran;
import org.noear.weed.DbTranUtil;

public class TranExcludeImp implements Tran {
    @Override
    public void execute(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        DbTran tran = DbTranUtil.current();
        try {
            //移除事务
            DbTranUtil.currentRemove();

            runnable.run();
        } finally {
            if (tran != null) {
                //把事务重新放回去
                //
                DbTranUtil.currentSet(tran);
            }
        }
    }
}
