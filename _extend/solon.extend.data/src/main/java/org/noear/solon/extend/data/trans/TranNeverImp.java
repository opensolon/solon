package org.noear.solon.extend.data.trans;

import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.Tran;
import org.noear.solon.extend.data.TranManager;

public class TranNeverImp implements Tran {
    public TranNeverImp() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        if (TranManager.current() != null) {
            //绝不能有事务
            throw new RuntimeException("Never support transactions");
        } else {
            runnable.run();
        }
    }
}

