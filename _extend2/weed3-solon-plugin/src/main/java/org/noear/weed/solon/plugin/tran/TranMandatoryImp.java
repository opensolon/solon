package org.noear.weed.solon.plugin.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbTranUtil;

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
            throw new RuntimeException("There has to be a transactions");
        } else {
            runnable.run();
        }
    }
}
