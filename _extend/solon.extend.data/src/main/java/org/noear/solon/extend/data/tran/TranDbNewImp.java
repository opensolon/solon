package org.noear.solon.extend.data.tran;


import org.noear.solon.core.Tran;
import org.noear.solon.core.TranSession;
import org.noear.solon.ext.RunnableEx;

public class TranDbNewImp extends DbTran implements Tran {
    public TranDbNewImp(TranSession session){
        super(session);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        DbTran tran = DbTranUtil.current();

        try {
            //移除事务
            DbTranUtil.currentRemove();

            super.execute(() -> {
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