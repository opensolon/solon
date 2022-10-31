package org.noear.solon.data.tranImp;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.data.tran.TranManager;

/**
 * 数据新事务实现。新建一个事务，同时将当前事务挂起（需要入栈）
 *
 * @author noear
 * @since 1.0
 * */
public class TranDbNewImp extends DbTran implements TranNode {

    public TranDbNewImp(Tran meta) {
        super(meta);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //尝试挂起事务
        //
        DbTran tran = TranManager.trySuspend();

        try {
            super.execute(() -> {
                runnable.run();
            });
        } finally {
            //尝试恢复事务
            TranManager.tryResume(tran);
        }
    }
}