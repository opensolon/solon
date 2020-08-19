package org.noear.solon.extend.mybatis.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranMandatoryImp implements Tran {
    protected TranMandatoryImp() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        Object tran = DbTranUtil.current();
        if (tran == null) {
            //必须要有事务
            throw new RuntimeException("You must have the same source transaction");
        } else {
            runnable.run();
        }
    }
}
