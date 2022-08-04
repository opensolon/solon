package org.noear.solon.data.tranImp;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.tran.TranNode;

/**
 * 数据事务实现。支持当前事务，如果没有则创建一个新的（需要入栈）
 *
 * @author noear
 * @since 1.0
 * */
public class TranDbImp extends DbTran implements TranNode {
    public TranDbImp(Tran meta) {
        super(meta);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        super.execute(() -> {
            runnable.run();
        });
    }
}
