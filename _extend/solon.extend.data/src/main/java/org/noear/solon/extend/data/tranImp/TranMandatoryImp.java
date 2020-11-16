package org.noear.solon.extend.data.tranImp;

import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranNode;
import org.noear.solon.extend.data.TranManager;

/**
 * 支持当前事务，如果没有事务则报错（不需要入栈）
 * */
public class TranMandatoryImp implements TranNode {
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
