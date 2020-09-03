package org.noear.solon.extend.data.tran;


import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranManager;

public class TranMandatoryImp implements Tran {
    public TranMandatoryImp() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        if (TranManager.current() == null) {
            //必须要有事务
            throw new RuntimeException("You must have the same source transaction");
        } else {
            runnable.run();
        }
    }
}
