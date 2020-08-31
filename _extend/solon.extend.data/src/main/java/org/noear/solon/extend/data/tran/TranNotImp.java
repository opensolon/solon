package org.noear.solon.extend.data.tran;


import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranNotImp implements Tran {
    public TranNotImp() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        Tran tran = DbTranUtil.current();

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
