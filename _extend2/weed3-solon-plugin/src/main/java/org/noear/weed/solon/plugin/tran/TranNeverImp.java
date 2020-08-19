package org.noear.weed.solon.plugin.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.weed.DbTran;
import org.noear.weed.DbTranUtil;

public class TranNeverImp implements Tran {
    protected TranNeverImp() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        Object tran = DbTranUtil.current();
        if (tran != null) {
            throw new RuntimeException("Never support transactions");
        } else {
            runnable.run();
        }
    }
}
