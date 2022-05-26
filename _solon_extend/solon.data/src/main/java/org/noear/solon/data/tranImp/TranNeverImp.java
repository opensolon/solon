package org.noear.solon.data.tranImp;

import org.noear.solon.ext.RunnableEx;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.data.tran.TranManager;

/**
 * 以无事务的方式执行，如果当前有事务则报错（不需要入栈）
 *
 * @author noear
 * @since 1.0
 * */
public class TranNeverImp implements TranNode {
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

